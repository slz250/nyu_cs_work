int f4(long x, long y)
{
	int result;
	int rbp10 = 0;
	int rbpc = 0;
	int rbp8 = 0;
	rbp8 = x ^ y;
	int edx;
	goto add_6d3;
	add_6b2:
		edx = 1;
		edx <<= rbpc;
		result = (edx & rbp8);
		if (result == 0) {
			goto add_6cf;
		}
		rbp10 += 1;
		rbpc += 2;
		goto add_6d3;
	add_6cf:
		rbpc += 2;
	add_6d3:
		if (rbpc <= 0x3f) {
			goto add_6b2;
		}
		if (x <= y) {
			goto add_6e9;
		}
		rbp10 = (0x70 | rbp10);
		goto add_6ed;
	add_6e9:
		rbp10 = (1 | rbp10);
		result = rbp10;
		return result;
	add_6ed:
		result = rbp10;
		return result;
}
