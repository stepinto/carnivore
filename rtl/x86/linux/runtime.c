#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// forward declaration
void *_mkstr(const char *);

// function print(s: string)
void _print(void *s) {
	int len = *(int*)s;
	s += sizeof(int);
	while (len-- > 0) {
		printf("%c", *(char*)s);
		s++;
	}
}

// function printi(n: int)
void _printi(int n) {
	printf("%d", n);
}

// function flush()
void _flush() {
	fflush(stdout);
}

// function getchar(): string
void *_getchar() {
	int ch = getc(stdin);
	char buf[2] = {0};

	if (ch < 0)
		return _mkstr("");
	else {
		buf[0] = (char)ch;
		return _mkstr(buf);
	}
}

// function geti(): int
int _geti() {
	int n;
	scanf("%d", &n);
	return n;
}

// ord(s: string): int
int _ord(void *s) {
	int len = *(int*)s;
	s += sizeof(int);
	if (len < 1)
		return -1;
	else
		return *(char*)s - '0';
}

// chr(n: int): s
void *_chr(int n) {
	char buf[2] = {0};
	buf[0] = '0' + n;
	return _mkstr(buf);
}

// size(s: string): int
int _size(void *s) {
	return *(int*)s;
}

// substring(s: string, f: int, n: int): string
void *_substring(void *s, int f, int n) {
	void *t = malloc(n + sizeof(int));
	*(int*)t = n;
	memcpy((char*)t+sizeof(int), (char*)f+f+sizeof(int), n);
	return t;
}

// concat(s1: string, s2: string): string
void *_concat(void *s1, void *s2) {
	int len1 = *(int*)s1;
	int len2 = *(int*)s2;
	int len = len1 + len2;
	void *t = malloc(len + sizeof(int));
	s1 += sizeof(int);
	s2 += sizeof(int);
	*(int*)t = len;
	memcpy((char*)t+sizeof(int), s1, len1);
	memcpy((char*)t+sizeof(int)+len1, s2, len2);
	return t;
}

// function not(n: int): int
int _not(int n) {
	return n == 0;
}

// function exit(n: int)
void _exit(int n) {
	exit(n);
}

// helper function
void *_mkstr(const char *s) {
	int len = strlen(s);
	void *t = malloc(len + sizeof(int));
	*(int*)t = len;
	memcpy((char*)t + sizeof(int), s, len);
	return t;
}

// void *_malloc(int size)
void *_malloc(int size) {
	return malloc(size);
}


// main
int main() {
	extern void _tig_start();
	_tig_start();
	_exit(0);
}

