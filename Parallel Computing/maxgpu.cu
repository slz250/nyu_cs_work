#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <cuda.h>

//function declarations
void populateArr(unsigned int* num);
unsigned int getmax(unsigned int* num, unsigned int size);
//

void populateArr(unsigned int* num, unsigned int size) {
  unsigned int i;
  srand(time(NULL)); // setting a seed for the random number generator
  // Fill-up te array with random num from 0 to size-1 
  for(i = 0; i < size; i++) {
    num[i] = rand() % size;
  }
}

/*
input: pointer to an array of unsigned int number of elements in the array
output: the maximum number of the array
*/
unsigned int getmax(unsigned int* num, unsigned int size) {
  unsigned int i;
  unsigned int max = num[0];

  for(i = 1; i < size; i++) {
    if(num[i] > max) {
      max = num[i];
    }
  }
  return (max);
}

__global__ void getmaxcu(unsigned int* numD, unsigned int numThreads) {
  unsigned int i , stride;
  unsigned int uniqueIndex = threadIdx.x + (blockDim.x * blockIdx.x);  

  stride = numThreads / 10;
  if (uniqueIndex < stride) {
    for (i = 1; i < 10; i++) {
      if (numD[uniqueIndex] < numD[uniqueIndex + stride * i]) {
        numD[uniqueIndex] = numD[uniqueIndex + stride * i];
      }
      __syncthreads();
    }
  }
  __syncthreads();
}

int main(int argc, char *argv[]) {
  unsigned int size = 0;  // The size of the array
  unsigned int* num; //pointer to the array

  if(argc !=2) {
    printf("usage: maxseq num\n");
    printf("num = size of the array\n");
    exit(1);
  }

  size = atol(argv[1]);

  num = (unsigned int *) malloc(size * sizeof(unsigned int));
  if(!num) {
    printf("Unable to allocate mem for an array of size %u\n", size);
    exit(1);
  }    

  populateArr(num, size);

  // printf("Ser max: %u\n", getmax(num, size));
  unsigned int threadsPerBlock = 1024;
  unsigned int numBlocks = (unsigned int) ceil((double) size/threadsPerBlock);
  unsigned int dataSize = size * sizeof(unsigned int);
  unsigned int* numD;

  cudaMalloc((void**) &numD, dataSize);
  cudaMemcpy(numD, num, dataSize, cudaMemcpyHostToDevice);

  unsigned int numThreads;

  for (numThreads = size; numThreads > 1; numThreads = numThreads / 10) {
    getmaxcu<<<numBlocks, threadsPerBlock>>>(numD, numThreads);
  }

  cudaMemcpy(num, numD, dataSize, cudaMemcpyDeviceToHost);

  printf("Par max: %u\n", num[0]);


  free(num);
  cudaFree(numD);
  exit(0);
}