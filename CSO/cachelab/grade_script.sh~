#!/usr/bin/bash
make
./cachesim  -s 5 -E 3 -b 4 -t ls.trace
./cache-ref -s 5 -E 3 -b 4 -t ls.trace
./cachesim  -s 5 -E 3 -b 4 -t lsmod.trace
./cache-ref -s 5 -E 3 -b 4 -t lsmod.trace
./cachesim  -s 5 -E 3 -b 4 -t clear.trace
./cache-ref -s 5 -E 3 -b 4 -t clear.trace
