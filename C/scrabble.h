#ifndef PARSER_H
#define PARSER_H 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <gtk/gtk.h>


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


gchar* forGtkView(char* grille,char*tirage);

char* forSendfromGtk(gchar* text);

void set_new_buffer_withmarkup(GtkTextBuffer* scrabble_text_view,char* grille,char* tirage);

void set_chat_text(GtkTextBuffer* chat_buffer,char* name,char* message);

#endif