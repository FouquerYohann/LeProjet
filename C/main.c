#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

int main(int argc, char const *argv[]){
	
	time_t temps1,temps2;
	temps1=time(NULL);
	sleep(5);
	temps2=time(NULL);
	printf("difference entre temps 1 et temps 2 %ld\n", (temps2-temps1));


	return 0;
}