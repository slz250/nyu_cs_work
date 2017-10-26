/*
Shi Liang Zhang
PC Lab 2
*/

#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

void printArr(int arr[], int primeUpperBound) {
	int i;
	for (i = 0; i <= primeUpperBound; i++) {
		printf("arr[%d]: %d ", i, arr[i]);
	}
	printf("\n");
}

void printInfo(int arr[], int primeUpperBound) {
	int i;
	int count = 1, prevNum = 2, gap;
	for (i = 2; i < primeUpperBound; i++) {
		if (arr[i] != 0) {
			gap = i - prevNum;
			prevNum = i;
			printf("%d, %d, %d\n", count++, i, gap);
		}
	}
}

void createOutputFile(int arr[], int primeUpperBound) {
	int i;
	char * filename[sizeof("1000000.txt")];
	sprintf(filename, "%d.txt", primeUpperBound);
	FILE * filePointer = fopen(filename, "w");
	
	int count = 1, prevNum = 2, gap;
	for (i = 2; i < primeUpperBound; i++) {
		if (arr[i] != 0) {
			gap = i - prevNum;
			prevNum = i;
			fprintf(filePointer, "%d, %d, %d\n", count++, i, gap);
		}
	}

	fclose(filePointer);
}

void getPrimes(int arr[], int primeUpperBound, int numThreads) {
	int i, numToCheck = 0, upperBound;
	upperBound = (primeUpperBound + 1) / 2;

	double tstart = omp_get_wtime();
	#pragma omp parallel for num_threads(numThreads) private(numToCheck)
	for (numToCheck = 2; numToCheck < primeUpperBound; numToCheck++) {	
		if (arr[numToCheck] == 1) {
			#pragma omp parallel for num_threads(numThreads) private(i)
			for (i = numToCheck + numToCheck; i < primeUpperBound; i += numToCheck) {
				arr[i] = 0;
			}
		}
	}
	double ttaken = omp_get_wtime() - tstart;
	printInfo(arr, primeUpperBound);
	printf("Time taken for the main part: %f\n", ttaken);
}

int main(int argc, char *argv[]) {
	int primeUpperBound, numThreads;

	if( argc != 3) {
		printf("Error.\n");
		exit(1);
	}

	primeUpperBound = atoi(argv[1]);
	numThreads = strtol(argv[2], NULL, 10);

	//init list 0 is nonprime, 1 is prime
	int i;
	int arr[primeUpperBound + 1];
	for (i = 2; i <= primeUpperBound; i++) {
		arr[i] = 1;
	}

	getPrimes(arr, primeUpperBound, numThreads);

	createOutputFile(arr, primeUpperBound);

	exit(0);
}