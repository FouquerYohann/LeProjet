#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "parser.h"

int main(int argc, char const *argv[]){
	// int i;
	// char* str = "0000000000000000000000000000000000000000000000000000000000000000000000H000000000000J0UVAL000000000E0I0Z000000000CUIT0I00000000000000M00000000000000U000000000SIEENT00000000000000000000000000000000000000000000000000000000000000UEDGFKS";

	// scrabble* sc =	initScrabble();
	// parseScrabble(sc,str);
	// printf("%s\n",toString(sc));

	// writeInFic(FIC,sc);


	// freeScrabble(sc);


	// sc=readInFic(FIC);

	// printf("%c\n", sc->grille[2][3]);

	// printf("----------------\n%s\n",toString(sc));


	// printf("\n---------------\nOUT\n%s\n",parseOut(sc) );

	// freeScrabble(sc);

	char* buf=strdup("CONNEXION/Yohann/25ans/Toutesesdents/");
	char** toks=(char**)malloc(6*sizeof(char*));
	char* tok;
	int i=0;
	const char split[2] = "/";
	tok=strtok(buf,split);

	while(tok!=NULL){
		free(toks[i]);
		toks[i++]=strdup(tok);
		tok=strtok(NULL,split);
	}
	int j;
	for(j=0;j<i;j++){
		printf("%s\n", toks[j]);
	}



	return 0;
}