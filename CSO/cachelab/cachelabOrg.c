/* Shi Zhang
 * N10038678
 *
 * cachelab.c - A cache simulator that can replay traces of memory accesses
 *     and output statistics such as number of hits, misses, and
 *     evictions.  The replacement policy is LRU.
 *
 * Implementation and assumptions:
 *  1. Each load/store can cause at most one cache miss. (I examined the trace,
 *  the largest request I saw was for 8 bytes).
 *  2. Instruction loads (I) are ignored.
 *  3. data modify (M) is treated as a load followed by a store to the same
 *  address. Hence, an M operation can result in two cache hits, or a miss and a
 *  hit plus a possible eviction. 
 */
#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <assert.h>
#include <math.h>
#include <limits.h>
#include <string.h>
#include <errno.h>

//#define DEBUG_ON 
#define ADDRESS_LENGTH 64

/* Type: Memory address */
typedef unsigned long long int mem_addr_t;

/**************************************************************************/
/* Declarations of the functions currently implemented in this file */

/* 
 * printSummary - This function provides a standard way for your cache
 * simulator * to display its final hit and miss statistics
 */ 
void printSummary(int hits,  /* number of  hits */
				  int misses, /* number of misses */
				  int evictions); /* number of evictions */

/*
 * replayTrace - replays the given trace file against the cache 
 */
void replayTrace(char* trace_fn);

/* 
 * accessData - Access data at memory address addr. 
 * This is the one that you need to implement.
 */
void accessData(mem_addr_t addr);

/*
 * printUsage - Print usage info
 */
void printUsage(char* argv[]);
/**************************************************************************/


/**************************************************************************/
/* Declarations of the global variable currently used. */
/* Do NOT modify any of the following globals. 
 * But you can add your own globals if you want 
 */
/* Globals set by command line args */
int s = 0; /* set index bits */
int b = 0; /* block offset bits */
int E = 0; /* associativity */
char* trace_file = NULL;

/* Counters used to record cache statistics */
int miss_count = 0;     /* cache miss */
int hit_count = 0;      /* cache hit */
int eviction_count = 0; /* A block is evicted from the cache */
/**************************************************************************/


/* 
 * accessData - Access data at memory address addr.
 *   If it is already in cache, increast hit_count
 *   If it is not in cache, bring it in cache, increase miss count.
 *   Also increase eviction_count if a line is evicted.
 * 
 *   If you need to evict a line (i.e. block) apply least recently used 
 *   replacement policy.
 */

typedef struct block {
	struct block *prev, *next;
	unsigned long long int tag;
} block;

typedef struct setQueue {
	unsigned long long int count;
	unsigned long long int numOfBlocks;
	block *front, *rear; 
} setQueue;

//array of setQueues with each setQueue being a set
typedef struct cache {
	unsigned long long int numOfSets;
	setQueue* *arrOfSets;
} cache;

cache* cacheOfSets;

//new block with tag
block* newBlock(unsigned long long int tag) {
	block* temp = (block*) malloc(sizeof(block)); 
	temp->tag = tag;

	temp->prev = temp->next = NULL;

	return temp;
}

setQueue* createSetQueue(unsigned long long int numOfBlocks) {
	setQueue* queueSet = (setQueue*) malloc(sizeof(setQueue));
	queueSet->count = 0;
	queueSet->front = queueSet->rear = NULL;
	queueSet->numOfBlocks = numOfBlocks;

	return queueSet;
}

cache* createCache(unsigned long long int numOfSets, unsigned long long int numOfBlocks) {
	cache* cacheOfSets = (cache*) malloc(sizeof(cache));
	cacheOfSets->numOfSets = numOfSets;
	cacheOfSets->arrOfSets = (setQueue**) malloc(cacheOfSets->numOfSets * sizeof(setQueue*));

	unsigned long long int i;
	for (i = 0; i < cacheOfSets->numOfSets; i++) {
		cacheOfSets->arrOfSets[i] = createSetQueue(numOfBlocks);
	}

	return cacheOfSets;
}

int isSetFull(setQueue* queueSet) {
	return (queueSet->count == queueSet->numOfBlocks);
}

int isSetEmpty(setQueue* queueSet) {
	return (queueSet->front == NULL);
}

void deQueue(setQueue* queueSet) {
	if(isSetEmpty(queueSet)) {
		return;
	}
	
	//if only block in set
	if (queueSet->front == queueSet->rear) {
		queueSet->front = NULL;
	}

	eviction_count++;
	block* temp = queueSet->rear;
	queueSet->rear = queueSet->rear->prev;

	if (queueSet->rear)
		queueSet->rear->next = NULL;

	free(temp);

	queueSet->count--;
}

void Enqueue(cache* cacheOfSets, unsigned long long int setNum, unsigned long long int tag) {
	setQueue* queueSet = cacheOfSets->arrOfSets[setNum];

	if(isSetFull(queueSet)) {
		deQueue(queueSet);
	}

	block* temp = newBlock(tag);
	temp->next = queueSet->front;

	if(isSetEmpty(queueSet)) {
		queueSet->rear = queueSet->front = temp;
	}
	
	else {
		queueSet->front->prev = temp; 
		queueSet->front = temp;
	}

	queueSet->count++;
}

void checkTag(cache* cacheOfSets, unsigned long long setIndex, unsigned long long int tag) {
	block* search = NULL;
	setQueue* queueSet = cacheOfSets->arrOfSets[setIndex];
	block* temp = queueSet->front;

	if(isSetFull(queueSet)) {
		while(temp->next != NULL) {
			if (temp->tag == tag) {
				search = temp;
				hit_count++;
			}
		
			temp = temp->next;
		}
	}
 
    // the page is not in cache, bring it
    if (search == NULL) {
	miss_count++;
        Enqueue(cacheOfSets, setIndex, tag);
    }

    // page is there and not at front, change pointer
    else if (search != queueSet->front)
    {
        // Unlink rquested page from its current location
        // in queue.
        search->prev->next = search->next;
        if (search->next)
           search->next->prev = search->prev;
 
        // If the requested page is rear, then change rear
        // as this node will be moved to front
        if (search == queueSet->rear)
        {
           queueSet->rear = search->prev;
           queueSet->rear->next = NULL;
        }
 
        // Put the requested page before current front
        search->next = queueSet->front;
        search->prev = NULL;
 
        // Change prev of current front
        search->next->prev = search;
 
        // Change front to the requested page
        queueSet->front = search;
    }
}

void accessData(mem_addr_t addr)
{
   /* You need to implement this function and any other function this
    * function may need 
    */

	unsigned long long int tag_bits = 64 - (s + b);
	unsigned long long int tag = addr >> (s + b);
	unsigned long long int setIndex = (addr << tag_bits) >> (tag_bits + b);

	checkTag(cacheOfSets, setIndex, tag);
}

/*
 * main - Main routine 
 */
int main(int argc, char* argv[])
{
    char c;

    /* Do NOT modify anything from this point till the following comment */
    while( (c=getopt(argc,argv,"s:E:b:t:vh")) != -1){
        switch(c){
        case 's':
            s = atoi(optarg);
            break;
        case 'E':
            E = atoi(optarg);
            break;
        case 'b':
            b = atoi(optarg);
            break;
        case 't':
            trace_file = optarg;
            break;
        case 'h':
            printUsage(argv);
            exit(0);
        default:
            printUsage(argv);
            exit(1);
        }
    }

    /* Make sure that all required command line args were specified */
    if (s == 0 || E == 0 || b == 0 || trace_file == NULL) {
        printf("%s: Missing required command line argument\n", argv[0]);
        printUsage(argv);
        exit(1);
    }
    /* From here you can make any modification, 
     * except removing the call to replayTrace */
	unsigned long long int S = 1 << s;
	cacheOfSets = createCache(S, E);
    
    /* Do not remove this line as it is the one calls your cache access function */
    replayTrace(trace_file);
    
  
    /* Do not modify anything from here till end of main() function */
    printSummary(hit_count, miss_count, eviction_count);
    return 0;
}


/****** Do NOT modify anything below this point ******/

/* 
 * printSummary - Summarize the cache simulation statistics. Student cache simulators
 *                must call this function in order to be properly autograded. 
 */
void printSummary(int hits, int misses, int evictions)
{
    printf("hits:%d misses:%d evictions:%d\n", hits, misses, evictions);
    
}

/*
 * printUsage - Print usage info
 */
void printUsage(char* argv[])
{
    printf("Usage: %s [-hv] -s <num> -E <num> -b <num> -t <file>\n", argv[0]);
    printf("Options:\n");
    printf("  -h         Print this help message.\n");
    printf("  -s <num>   Number of set index bits.\n");
    printf("  -E <num>   Number of blocks per set (i.e. associativity).\n");
    printf("  -b <num>   Number of block offset bits.\n");
    printf("  -t <file>  Trace file.\n");
    printf("\nExamples:\n");
    printf("  linux>  %s -s 4 -E 1 -b 4 -t ls.trace\n", argv[0]);
    exit(0);
}

/*
 * replayTrace - replays the given trace file against the cache 
 */
void replayTrace(char* trace_fn)
{
    char buf[1000];
    mem_addr_t addr=0;
    unsigned int len=0;
    FILE* trace_fp = fopen(trace_fn, "r");

    if(!trace_fp){
        fprintf(stderr, "%s: %s\n", trace_fn, strerror(errno));
        exit(1);
    }

    while( fgets(buf, 1000, trace_fp) != NULL) {
        if(buf[1]=='S' || buf[1]=='L' || buf[1]=='M') {
            sscanf(buf+3, "%llx,%u", &addr, &len);

            accessData(addr);

            /* If the instruction is R/W then access again */
            if(buf[1]=='M')
                accessData(addr);
            
        }
    }

    fclose(trace_fp);
}
