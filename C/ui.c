#include "ui.h"

pthread_mutex_t scrabble_view_mutex=PTHREAD_MUTEX_INITIALIZER;


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

	
	return  retour;
}


void set_new_buffer_withmarkup(GtkTextBuffer* scrabble_buffer,char* grille,char* tirage){
	char* tex=forGtkView(grille,tirage);
	GtkTextIter start,end;

	pthread_mutex_lock(&scrabble_view_mutex);
	gtk_text_buffer_set_text(scrabble_buffer,"",0);

	gtk_text_buffer_get_bounds (scrabble_buffer,&start,&end);

	gtk_text_buffer_insert_markup(scrabble_buffer,&end,tex,strlen(tex));

	pthread_mutex_unlock(&scrabble_view_mutex);

	
}

void set_chat_text(GtkTextBuffer* chat_buffer,char* name,char* message, int prive){
	char buf[1024];
	if(prive)
		sprintf(buf,"private -- %s : %s\n",name,message);
	else
		sprintf(buf,"%s : %s\n",name,message);

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