#ifndef SCRABBLE_H
#define SCRABBLE_H 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>



#ifndef VIDE
#define VIDE '_'
#endif



#ifndef FIC
#define FIC "scrabble.scrabble"
#endif


typedef struct scrabble
{
	char* tirage;
	char** grille;
}scrabble;

scrabble* initScrabble();

void freeScrabble(scrabble* sc);

char* toString(scrabble* sc);

void parseScrabble(scrabble* sc,char* toParse);

void setGrille(scrabble* sc,char* toParse);

void setTirage(scrabble* sc,char* toParse);

void writeInFic(char* path,scrabble* sc);

scrabble* readInFic(char* path);

char* parseOut(scrabble* sc);


#endif