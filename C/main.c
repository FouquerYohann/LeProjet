#include <stdio.h>
#include <stdlib.h>
#include "parser.h"

int main(int argc, char const *argv[]){
	int i;
	char* str = "0000000000000000000000000000000000000000000000000000000000000000000000H000000000000J0UVAL000000000E0I0Z000000000CUIT0I00000000000000M00000000000000U000000000SIEENT00000000000000000000000000000000000000000000000000000000000000UEDGFKS";

	scrabble* sc =	initScrabble();
	parseScrabble(sc,str);
	printf("%s\n",toString(sc));

	writeInFic(FIC,sc);


	freeScrabble(sc);


	sc=readInFic(FIC);

	printf("%c\n", sc->grille[2][3]);

	printf("----------------\n%s\n",toString(sc));


	printf("\n---------------\nOUT\n%s\n",parseOut(sc) );

	freeScrabble(sc);
	return 0;
}