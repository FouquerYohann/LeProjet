#include "scrabble.h"

scrabble* initScrabble(){
	int i ;
	scrabble* ret_sc;
	ret_sc=malloc(sizeof(scrabble));
	char* tirage = (char*)malloc(sizeof(char)*7);
	char** grille = (char**)malloc(sizeof(char*)*15);

	for (i = 0; i < 15; ++i){
		grille[i] = (char*)malloc(sizeof(char)*15);
	}
	ret_sc->tirage = tirage;
	ret_sc->grille = grille;
	return ret_sc;
}


void freeScrabble(scrabble* sc){
	int i ;
	char** grille=sc->grille;
	for (i = 0; i < 15; ++i){
		free(grille[i]);
	}
	free(sc->grille);
	free(sc->tirage);
	free(sc);
}

char* toString(scrabble* sc){
	int j;
	int i;
	int iter=0;
	char* retS=(char*)malloc(sizeof(char)*249);
	char** grille=sc->grille;
	char*  tirage=sc->tirage;
	for (j = 0; j < 15; ++j){
		for (i = 0; i < 15; ++i){
			retS[iter]=grille[j][i];
			iter++;
		}
		retS[iter]='\n';
		iter++;
	}
	for (i = 0; i < 7; ++i){
		retS[iter]=tirage[i];
		iter++;
	}
	return retS;
}


void parseScrabble(scrabble* sc,char* toParse){
	int j;
	int i;
	int iter=0;
	char** grille=sc->grille;
	char*  tirage=sc->tirage;
	for (j = 0; j < 15; ++j){
		for (i = 0; i < 15; ++i){
			if(toParse[iter]=='0')
				grille[j][i]=VIDE;
			else
				grille[j][i]=toParse[iter];
			iter++;
		}
	}
	if(!(toParse[iter++]=='/')){
		fprintf(stderr, "Probleme dans le parseScrabble en C\n");
	}
	for (i = 0; i < 7; ++i){
		tirage[i]=toParse[iter];
		iter++;
	}
}


void writeInFic(char* path,scrabble* sc){
	FILE* f = fopen(path,"w");
	fprintf(f, "%s\n", toString(sc));
	fclose(f);
}

scrabble* readInFic(char* path){
	int i,j;
	scrabble* ret=initScrabble();
	char buf[16];
	FILE* f = fopen(path,"r");
	for (i = 0; i < 15; ++i){
		fgets(buf,17,f);
		char* ligne=ret->grille[i];
		for (j = 0; j < 15; ++j){
			ligne[j]=buf[j];
		}
	}
	fgets(buf,8,f);
	char* tirage = ret->tirage;
	for (i = 0; i < 7; ++i){
		tirage[i]=buf[i];
	}

	return ret;
}


char* parseOut(scrabble* sc){
	int j;
	int i;
	int iter=0;
	char* retS=(char*)malloc(sizeof(char)*234);
	char** grille=sc->grille;
	char*  tirage=sc->tirage;
	for (j = 0; j < 15; ++j){
		for (i = 0; i < 15; ++i){
			retS[iter]=(grille[j][i]==VIDE)?'0':grille[j][i];
			iter++;
		}
	}
	retS[iter++]='/';
	for (i = 0; i < 7; ++i){
		retS[iter]=tirage[i];
		iter++;
	}
	retS[iter]='\0';
	return retS;
}