#include <stdio.h>
#include <stdlib.h>

int f2(long x, long y) {
	int rbpc = 0; //maybe 2 bytes instead (word)
	int a;
	a = y + x;
	a >>= 2;
	int rbp4 = a; 
	int rbp8 = 0;
	goto address_6c1;

	address_6c1:
	a = rbp8;
	if (a < rbp4) {
		goto address_6a3;
	}
	goto address_6ce;

	address_6a3: //DONE
		a = 1 & rbp8; //CHECK
		if (a != 0) {
			goto address_6b6;
		}
		rbpc += x;
		goto address_6bd;

	address_6b6: //DONE
		rbpc -= y; 
		rbp8 += 1;
		if (rbp8 < rbp4) {
			goto address_6a3;
		}
		goto address_6ce;
	address_6bd: //DONE
		rbp8 += 1;
		if (rbp8 < rbp4) {
			goto address_6a3;
		}
		goto address_6ce;
	address_6ce: //DONE
		if (y < rbpc) {
			goto address_6cb;
		}
		a = rbpc;
		return a;
	address_6cb: //DONE
		rbpc >>= 1;
		if (y < rbpc) {
			goto address_6cb;
		}		
		a = rbpc;
		return a;

}
