#include "scrabble.h"
#include <pthread.h>

pthread_mutex_t scrabble_view_mutex=PTHREAD_MUTEX_INITIALIZER;

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


void setGrille(scrabble* sc,char* toParse){
	int j;
	int i;
	int iter=0;
	char** grille=sc->grille;
	for (j = 0; j < 15; ++j){
		for (i = 0; i < 15; ++i){
			if(toParse[iter]=='0')
				grille[j][i]=VIDE;
			else
				grille[j][i]=toParse[iter];
			iter++;
		}
	}
}
void setTirage(scrabble* sc,char* toParse){
	int i;
	for (i = 0; i < strlen(toParse) || i < 7; ++i){
		sc->tirage[i]=(toParse[i]==VIDE)?0:toParse[i];
	}
	for(int j=i;j<7;j++){
		sc->tirage[j]=VIDE;
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
	for (j = 0; j < 15; ++j){
		for (i = 0; i < 15; ++i){
			retS[iter]=(grille[j][i]==VIDE)?'0':grille[j][i];
			iter++;
		}
	}
	retS[iter]='\0';
	return retS;
}


char* forSendfromGtk(char* text){
	char* retour=(char*)malloc(256*sizeof(char));
	int iter=0;
	for (int i = 0; i < strlen(text); ++i)
	{
		if(text[i]!='\n'){
			retour[iter]=(text[i]==VIDE)?'0':text[i];
			iter++;
		}
		if(iter==225)
			break;	
	}
	retour[iter]='\0';
	return retour;
}

char* forGtkView(char* grille,char *tirage){
	char* retour=(char*)malloc(1024*sizeof(char));

	
	sprintf(retour,"<tt>");
	int iter=strlen(retour);
	for (int i = 0; i < 15; ++i)
	{
		for (int j = 0; j < 15; ++j)
		{
			retour[iter++]=(grille[i*15+j]=='0')?VIDE:grille[i*15+j];
			if(i==7 && j== 7 && retour[iter-1]==VIDE)
				retour[iter-1]='0';
		}
		retour[iter++]='\n';
	}

	
	for(int i=0;i<7;i++){
		retour[iter++]=tirage[i];
	}
	retour[strlen(retour)]='\0';
	sprintf(retour,"%s</tt>",retour);
	retour[strlen(retour)]='\0';

	printf("retour : \n%s\n", retour);
	return  retour;
}


void set_new_buffer_withmarkup(GtkTextBuffer* scrabble_buffer,char* grille,char* tirage){
	char* tex=forGtkView(grille,tirage);
	GtkTextIter start,end;

	pthread_mutex_lock(&scrabble_view_mutex);
	gtk_text_buffer_set_text(scrabble_buffer,"",0);

	gtk_text_buffer_get_bounds (scrabble_buffer,&start,&end);
	printf("TEXXXXXX : \n%s\n", tex);
	gtk_text_buffer_insert_markup(scrabble_buffer,&end,tex,strlen(tex));

	pthread_mutex_unlock(&scrabble_view_mutex);

	
}

void set_chat_text(GtkTextBuffer* chat_buffer,char* name,char* message){
	char buf[1024];
	sprintf(buf,"%s : %s",name,message);
	GtkTextIter start,end;
	if(gtk_text_buffer_get_line_count(chat_buffer) >15){
		gtk_text_buffer_get_bounds (chat_buffer,&start,&end);
		GtkTextIter tmp=start;
		gtk_text_iter_forward_line(&tmp);
		gtk_text_buffer_delete(chat_buffer,&start,&tmp);

	}

	gtk_text_buffer_get_bounds (chat_buffer,&start,&end);
	gtk_text_buffer_insert(chat_buffer,&end,buf,strlen(buf));

}