#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// forward declaration
void *rtl_mkstr(const char *);

// function print(s: string)
void rtl_print(void *s) {
	int len = *(int*)s;
	s += sizeof(int);
	while (len-- > 0) {
		printf("%c", *(char*)s);
		s++;
	}
}

// function printi(n: int)
void rtl_printi(int n) {
	printf("%d", n);
}

// function flush()
void rtl_flush() {
	fflush(stdout);
}

// function getchar(): string
void *rtl_getchar() {
	int ch = getc(stdin);
	char buf[2] = {0};

	if (ch < 0)
		return rtl_mkstr("");
	else {
		buf[0] = (char)ch;
		return rtl_mkstr(buf);
	}
}

// function geti(): int
int rtl_geti() {
	int n;
	scanf("%d", &n);
	return n;
}

// ord(s: string): int
int rtl_ord(void *s) {
	int len = *(int*)s;
	s += sizeof(int);
	if (len < 1)
		return -1;
	else
		return *(char*)s - '0';
}

// chr(n: int): s
void *rtl_chr(int n) {
	char buf[2] = {0};
	buf[0] = '0' + n;
	return rtl_mkstr(buf);
}

// size(s: string): int
int rtl_size(void *s) {
	return *(int*)s;
}

// substring(s: string, f: int, n: int): string
void *rtl_substring(void *s, int f, int n) {
	void *t = malloc(n + sizeof(int));
	*(int*)t = n;
	memcpy((char*)t+sizeof(int), (char*)f+f+sizeof(int), n);
	return t;
}

// concat(s1: string, s2: string): string
void *rtl_concat(void *s1, void *s2) {
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
int rtl_not(int n) {
	return n == 0;
}

// function exit(n: int)
void rtl_exit(int n) {
	exit(n);
}

// function _strcmp(a: string, b: string): int
int rtl_strcmp(void *s1, void *s2) {
	int len1 = *(int*)s1;
	int len2 = *(int*)s2;
	int i;

	s1 += sizeof(int);
	s2 += sizeof(int);
	for (i = 0; i < min(len1, len2); i++) {
		if (((char*)s1)[i] < ((char*)s2)[i])
			return -1;
		else if (((char*)s1)[i] > ((char*)s2)[i])
			return 1;
	}

	if (len1 < len2)
		return -1;
	else if (len1 > len2)
		return 1;
	else
		return 0;
}

// helper function
void *rtl_mkstr(const char *s) {
	int len = strlen(s);
	void *t = malloc(len + sizeof(int));
	*(int*)t = len;
	memcpy((char*)t + sizeof(int), s, len);
	return t;
}

// void *_malloc(int size)
void *rtl_malloc(int size) {
	return malloc(size);
}

int min(int a, int b) {
	return a < b ? a : b;
}


// main
int main() {
	extern void tig_start();
	tig_start();
	exit(0);
}

