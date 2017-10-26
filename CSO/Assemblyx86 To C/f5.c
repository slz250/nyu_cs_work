int f53(long x, long y) { //y > x
	int a; //result
	int rbp8 = 0; 
	int rbp4 = 0;
	int num1 = 0x3e;
	int num2 = 0x1;

	while (rbp4 <= num1) {

		if (((y >> rbp4) & num2) == 0) {
		}

		else {
			rbp8 += 1;
		}

		rbp4 += 1;
	}

	a = rbp8;
	return a;
}

int f51(long x, long y) { // x > y
	long a; //tmp = a
	int rbp4 = 0;
	a = x;
	long edx = a;
	a = y;
	int ecx = edx + a * 1; 
	edx = 0x66666667;
	a = ecx; //a = x + y;
	a = a * edx; //result
	edx = a >> 32; //CHECK
	edx >>= 1;
	a = ecx;
	a >>= 0x1f;
	edx -= a;
	a = edx;
	rbp4 = a;
	edx = rbp4; 
	a = edx;
	a <<= 0x2;
	a += edx;
	ecx-= a; //CHECK
	a = ecx;
	rbp4 = a;

	if (rbp4 > 0x5) {
		goto add_77f;
	}
	a = rbp4; //TRY W AND W.O
	/*a = (0x400928 + a * 8);
	goto *a;
	a = 0x11;
	goto add_784;*/
		
	switch (rbp4) {
		case 0:
			return 0x11;
		case 1:
			return 0x12;
		case 2:
			return 0x13;
		case 3:
			return 0x14;
		case 4:
			return 0x15;
		case 5:
			return 0x16;
		/*default:
			return 0xa;*/
	}

	add_77f:
		a = 0xa;
		return a;
		
}

int f52(long x, long y) {
	int a;
	int rbpc = 0;
	int rbp8 = 0;
	int rdx;
	int ecx;

	goto add_7c4;

	add_7c4: //DONE
		if (rbp8 <= 0x3f) { 
			goto add_7a3;
		}
		a = rbpc;
		return a;

	add_7a3: //DONE
		rdx = x;
		a = rbp8;
		ecx = a;
		rdx >>= ecx;
		a = rdx;
		a = a & 0x1;
		if (a == 0) {
			goto add_7bf;
		}
		rbpc += 1;

	add_7bf: //DONE
		rbp8 += 1;

	goto add_7c4;
}

int f5(long x, long y)
{
	int rbp4 = 0;
	int a = x;
	if (a <= y) { //DONE
		goto add_6b3;
	}
	//rdx = y
	//rax = x
	a = 0;
	rbp4 = f51(x, y);
	goto add_6f5;

	add_6b3: //DONE
		a = x;
		if (a != y) { 
			goto add_6da;
		}
		a = 0;
		rbp4 = f52(x, y);
		goto add_6f5;
	
	add_6da: //DONE
		//rdx = y
		//rax = x
		//rsi = rdx = y
		//rdi = rax = x
		a = 0;
		rbp4 = f53(x, y);

	add_6f5: //DONE
		a = rbp4;
		return a;
}
