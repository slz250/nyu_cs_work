int f3(long x, long y)
{
	int rbp4 = 0;
	int a = y + x;
	//rax = x
	//edx = eax
	//rax = y
	//eax += edx 
	rbp4 = a;
	rbp4 <<= 1;
	rbp4 += 17;
	//edx = rbp4;
	//eax = edx;
	//eax += eax;
	//eax += edx;
	a = 3 * rbp4;
	rbp4 = a;
	a = x;

	if (a <= y) {
		goto add_6c0;
	}

	a = x;
	a >>= 2;
	rbp4 += a;
	goto add_6da;

	add_6c0:
		a = x;
		if (a != y) {
			goto add_6d0;
		}
		rbp4 -= 0x3c;
		goto add_6da;

	add_6d0:
		a = y;
		a >>= 2;
		rbp4 += a;

	add_6da:
		a = rbp4;
		return a;
}
