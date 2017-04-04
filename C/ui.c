#include "ui.h"



char* forSendfromGtk(gchar* text){
	char* retour=(char*)malloc(256*sizeof(char));
	int iter=0;
	for (int i = 0; i < 15; ++i)
	{
		for (int j = 0; j < 15; ++j)
		{
			retour[iter]=text[i];
			iter++;
		}
	}

	return retour;
}

gchar* forGtkView(char* grille,char*tirage){
	gchar* retour=(gchar*)malloc(1024*sizeof(gchar));
	
	int iter=0;
	for (int i = 0; i < 15; ++i)
	{
		for (int j = 0; j < 15; ++j)
		{
			retour[iter++]=(grille[i*15+j]=='0')?'_':grille[i*15+j];
		}
		retour[iter++]='\n';
	}

	
	for(int i=0;i<7;i++){
		retour[iter++]=tirage[i];
	}
	retour[iter]='\0';

	printf("%s\n", retour);
	return retour;
}