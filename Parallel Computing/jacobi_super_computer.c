/***
 Shiliang Zhang
 N10038678
***/
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <mpi.h>

/***** Globals ******/
float **a; /* The coefficients */
float *x;  /* The unknowns */
float *b;  /* The constants */
float err; /* The absolute relative error */
int num = 0;  /* number of unknowns */


/****** Function declarations */
void check_matrix(); /* Check whether the matrix will converge */
void get_input();  /* Read input from file */

/********************************/



/* Function definitions: functions are ordered alphabetically ****/
/*****************************************************************/

/*
 Conditions for convergence (diagonal dominance):
 1. diagonal element >= sum of all other elements of the row
 2. At least one diagonal element > sum of all other elements of the row
 */
void check_matrix()
{
    int bigger = 0; /* Set to 1 if at least one diag element > sum  */
    int i, j;
    float sum = 0;
    float aii = 0;
    
    for(i = 0; i < num; i++)
    {
        sum = 0;
        aii = fabs(a[i][i]);
        
        for(j = 0; j < num; j++)
            if( j != i)
                sum += fabs(a[i][j]);
            
            if( aii < sum)
            {
                printf("The matrix will not converge.\n");
                exit(1);
            }
            
            if(aii > sum)
                bigger++;
            
        }
        
        if( !bigger )
        {
            printf("The matrix will not converge\n");
            exit(1);
        }
    }


/******************************************************/
/* Read input from file */
/* After this function returns:
 * a[][] will be filled with coefficients and you can access them using a[i][j] for element (i,j)
 * x[] will contain the initial values of x
 * b[] will contain the constants (i.e. the right-hand-side of the equations
 * num will have number of variables
 * err will have the absolute error that you need to reach
 */
    void get_input(char filename[])
    {
        FILE * fp;
        int i,j;
        
        fp = fopen(filename, "r");
        if(!fp)
        {
            printf("Cannot open file %s\n", filename);
            exit(1);
        }
        
        fscanf(fp,"%d ",&num);
        fscanf(fp,"%f ",&err);
        
    /* Now, time to allocate the matrices and vectors */
        a = (float**)malloc(num * sizeof(float*));
        if( !a)
        {
            printf("Cannot allocate a!\n");
            exit(1);
        }
        
        for(i = 0; i < num; i++)
        {
            a[i] = (float *)malloc(num * sizeof(float));
            if( !a[i])
            {
                printf("Cannot allocate a[%d]!\n",i);
                exit(1);
            }
        }
        
        x = (float *) malloc(num * sizeof(float));
        if( !x)
        {
            printf("Cannot allocate x!\n");
            exit(1);
        }
        
        
        b = (float *) malloc(num * sizeof(float));
        if( !b)
        {
            printf("Cannot allocate b!\n");
            exit(1);
        }
        
    /* Now .. Filling the blanks */
        
    /* The initial values of Xs */
        for(i = 0; i < num; i++)
            fscanf(fp,"%f ", &x[i]);
        
        for(i = 0; i < num; i++)
        {
            for(j = 0; j < num; j++)
                fscanf(fp,"%f ",&a[i][j]);
            
        /* reading the b element */
            fscanf(fp,"%f ",&b[i]);
        }
        
        fclose(fp);
        
    }


/************************************************************/

    int main(int argc, char *argv[])
    {
        
        int k;
    int nit = 0; /* number of iterations */
        
        
        if( argc != 2)
        {
            printf("Usage: gsref filename\n");
            exit(1);
        }
        
    /* Read the input file and fill the global data structure above */
        get_input(argv[1]);
        
    /* Check for convergence condition */
    /* This function will exit the program if the coffeicient will never converge to
     * the needed absolute error.
     * This is not expected to happen for this programming assignment.
     */
        check_matrix();
        
    /* Writing to the stdout */
    /* Keep that same format */
        
    //init MPI
        int comm_sz;
        int my_rank;
        MPI_Init(&argc, &argv);
        MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);
        MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
        
    //divide among procs
        int equsPerProc = num / comm_sz;
        int my_first_i = my_rank * equsPerProc;
        int my_last_i = my_first_i + equsPerProc;
        
        int i, j, uIndex;
        float newUnknown[equsPerProc];
        float newErr, currMaxErr;
        
        do {
            currMaxErr = 0;
            nit++;
            uIndex = 0;
            
        //for the equations that we are tackling
            for (i = my_first_i; i < my_last_i; i++) {
                newUnknown[uIndex] = b[i];
                for (j = 0; j < num; j++) {
                    if (i == j) {
                        continue;
                    }
                    
                    newUnknown[uIndex] = newUnknown[uIndex] - (a[i][j] * x[j]);
                }
                
                if (a[i][i] == 0) {
                    printf("ZERO");
                }
                newUnknown[uIndex] = newUnknown[uIndex] / a[i][i];
                newErr = (float) ((newUnknown[uIndex] - x[i]) / newUnknown[uIndex]);
                
                if (newErr < 0) {
                    newErr = newErr * -1;
                }
                
                if (newErr > currMaxErr) {
                    currMaxErr = newErr;
                }
                
                uIndex++;
            }
            
            MPI_Allgather(newUnknown, equsPerProc, MPI_FLOAT, x, equsPerProc, MPI_FLOAT, MPI_COMM_WORLD);
            
        //sending currMaxErr of the proc
            if (my_rank != 0) {
                MPI_Send(&currMaxErr, 1, MPI_FLOAT, 0, 0, MPI_COMM_WORLD);
            }
            
        //receiving currMaxeErr of procs
            
            if (my_rank == 0) {
                float finalMaxErr = currMaxErr;
                for (i = 1; i < comm_sz; i++) {
                    MPI_Recv(&currMaxErr, 1, MPI_FLOAT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                    if (currMaxErr > finalMaxErr) {
                        finalMaxErr = currMaxErr;
                    }
                }
                currMaxErr = finalMaxErr;
                MPI_Bcast(&currMaxErr, 1, MPI_FLOAT, 0, MPI_COMM_WORLD);
            }
            
            if(my_rank != 0) {
                MPI_Bcast(&currMaxErr, 1, MPI_FLOAT, 0, MPI_COMM_WORLD);
            }

            MPI_Barrier(MPI_COMM_WORLD);
        } while (currMaxErr > err);
        
        MPI_Finalize();
        
        if (my_rank == 0) {
            for( i = 0; i < num; i++)
                printf("%f\n",x[i]);
            
            printf("total number of iterations: %d\n", nit);
        }
        
        exit(0);
    }
