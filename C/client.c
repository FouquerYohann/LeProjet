#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <gtk/gtk.h>
#include <pthread.h>
#include <time.h>

#include "client.h"
#include "partie.h"
#include "ui.h"

#define KGRN  "\x1B[32m"
#define KWHT  "\x1B[37m"

char* user_name;
pthread_t thread_server;
SOCKET sock ;
GtkBuilder* build;
partie* p;

char buffer[BUF_SIZE];
char ** toks;
int nb_args=0;
const char split[2] = "/";

int string_split(char** tabs,char* string_to_split,const char* delim ){

	int retour=0;
	char* tok=strtok(string_to_split,delim);

	while(tok!=NULL){
		//if(tabs[retour] !=NULL)
			//free(tabs[retour]);
		tabs[retour++]=strdup(tok);
		tok=strtok(NULL,delim);
	}
	return retour;
}


G_MODULE_EXPORT void on_click_trouve(GtkWidget *widget, gpointer data)
{
	// GtkTextView* scrabble_text_view=gtk_builder_get_object(builder,"scrabble_text_view");
	GtkTextIter start,end;
	GtkTextView* scrabble_text_view=(GtkTextView*) data;
	GtkTextBuffer* textBuffer=gtk_text_view_get_buffer(scrabble_text_view);
	gchar* text;

	gtk_text_buffer_get_bounds (textBuffer,&start,&end);
	text = gtk_text_buffer_get_text (textBuffer,&start,&end,FALSE);

	char* toSend=forSendfromGtk(text);
	printf("%s\n", toSend);

	sprintf(buffer,"TROUVE/%s\n",toSend);

	write_server(sock,buffer);
}

G_MODULE_EXPORT void on_click_send(GtkWidget *widget, gpointer data)
{

	GtkEntry* chat=(GtkEntry*)data;
	const gchar* text=gtk_entry_get_text(chat);

	sprintf(buffer,"MESSAGE/%s\n",text);
	gtk_entry_set_text(chat,"");
	write_server(sock,buffer);
}

G_MODULE_EXPORT void on_enter(GtkWidget *widget, gpointer data)
{

	GtkEntry* chat=(GtkEntry*)widget;
	const gchar* text=gtk_entry_get_text(chat);

	sprintf(buffer,"MESSAGE/%s\n",text);
	gtk_entry_set_text(chat,"");
	write_server(sock,buffer);
}

G_MODULE_EXPORT void on_click_sort(GtkWidget *widget, gpointer data)
{
	sprintf(buffer,"SORT/%s/\n",p->user->name);
	write_server(sock,buffer);
	end_connection(sock);
	gtk_main_quit();
}

G_MODULE_EXPORT void on_destroy(GtkWidget *widget, gpointer data)
{
	sprintf(buffer,"SORT/%s/\n",p->user->name);
	write_server(sock,buffer);
	end_connection(sock);
	gtk_main_quit();
}








static void* app(void* data)
{
	time_t temps1,temps2;
	char timer[16];
	GtkBuilder* builder 			= (GtkBuilder*)  data;
	GtkLabel* phase_label           = (GtkLabel*)    gtk_builder_get_object(builder,"phase_label");
	GtkLabel* score_label           = (GtkLabel*)    gtk_builder_get_object(builder,"score_label");
	GtkLabel* chrono_label          = (GtkLabel*)    gtk_builder_get_object(builder,"chrono_label");
	GtkLabel* best_player_label     = (GtkLabel*)    gtk_builder_get_object(builder,"best_player_label");
	GtkTextView* scrabble_text_view = (GtkTextView*) gtk_builder_get_object(builder,"scrabble_text_view");
	GtkTextBuffer* scrabble_buffer  =                gtk_text_view_get_buffer(scrabble_text_view);
	GtkTextView* chat_text_view     = (GtkTextView*) gtk_builder_get_object(builder,"chat_text_view");
	GtkTextBuffer* chat_text_buffer =                gtk_text_view_get_buffer(chat_text_view);

	fd_set rdfs;
	
	temps1=time(NULL);

	while(1)
	{
		FD_ZERO(&rdfs);

		FD_SET(sock, &rdfs);

		
		if(select(sock + 1, &rdfs, NULL, NULL, NULL) == -1)
		{
			perror("select()");
			exit(errno);
		}

		if(FD_ISSET(sock,&rdfs)){
			

			int n = read_server(sock, buffer);

			#if DEBUG
			printf("\n %s\n",KGRN);
			printf("\n RECEIVING buffer : %s\n", buffer);
			printf("\n %s\n",KWHT);
			#endif

			/* server down */
			if(n == 0)
			{
				printf("Server disconnected !\n");
				break;
			}

			nb_args=string_split(toks,buffer,split);


			if(strcmp(toks[0],"CONNECTE")==0){
				printf("Un nouveau joueur est arrivé : %s\n", toks[1]);
				p->other_users=addUserNameScore(p->other_users,toks[1],"0");
			}
			else if(strcmp(toks[0],"DECONNEXION")==0){
				printf("Le joueur %s s'est deconnecte\n", toks[1]);
				p->other_users=deleteUser(p->other_users,getUserByName(p->other_users,toks[1]));

			}
			else if(strcmp(toks[0],"SESSION")==0){
				printf("debut d'une nouvelle session\n");
				p->sc=initScrabble();
				
				gtk_label_set_text(phase_label,"phase : Recherche");
			}
			else if(strcmp(toks[0],"VAINQUEUR")==0){
				printf("la partie est fini.\n Le bilan %s",toks[1]);
			}
			else if(strcmp(toks[0],"TOUR")==0){
				printf("un nouveau tour. plateau %s tirage %s\n",toks[1],toks[2]);
	
				setGrille(p->sc,toks[1]);
				setTirage(p->sc,toks[2]);

				gtk_label_set_text(phase_label,"phase : Recherche");
				set_new_buffer_withmarkup(scrabble_buffer,strdup(toks[1]),strdup(toks[2]));

			}
			else if(strcmp(toks[0],"RVALIDE")==0){
				printf("Placement valide, fin de la p->phase de recherche\n");
				gtk_label_set_text(phase_label,"phase : Soumission");
				
			}
			else if(strcmp(toks[0],"RINVALIDE")==0){
				printf("placement invalide pour la raison %s\n",toks[1]);
			}
			else if(strcmp(toks[0],"RATROUVE")==0){
				printf("le joueur %s a trouve un mot fin de la phase de recherche\n", toks[1]);
			}
			else if(strcmp(toks[0],"RFIN")==0){
				printf("expiration du delai de recherche, fin de la phase de recherche\n");
				temps1=time(NULL);
				
				gtk_label_set_text(phase_label,"phase : Soumission");
			}
			else if(strcmp(toks[0],"SVALIDE")==0){
				printf("soumission valide\n");
			}
			else if(strcmp(toks[0],"SINVALIDE")==0){
				printf("soumission invalide pour la raison %s\n",toks[1] );
			}
			else if(strcmp(toks[0],"SFIN")==0){
				printf("expiration du delai de soumission fin de la phase de soumission\n");
				temps1=time(NULL);
				gtk_label_set_text(phase_label,"phase : Resultat");
			}	
			else if(strcmp(toks[0],"BILAN")==0){
				printf("bilan du tour mot %s par %s \n",toks[1],toks[2]);
				temps1=time(NULL);
				char* tmp=strdup(toks[3]);
				nb_args=string_split(toks,tmp,"*");
				int i;
				printf("Scores :");
				for(i=1; i< nb_args;i=i+2){
					printf("\t %s : %s\n",toks[i],toks[i+1] );
					if(strcmp(toks[i],user_name)==0){
						char scor[64];
						sprintf(scor,"Votre score : %s",toks[i+1]);
						gtk_label_set_text(score_label,scor);
					}
				}
				gtk_label_set_text(best_player_label,"Soumettez un mot");
			}else if (strcmp(toks[0],"RETMESSAGE")==0){
				printf("Recu un nouveau message de %s : %s \n", toks[1],toks[2]);
				set_chat_text(chat_text_buffer,toks[1],toks[2]);

			} else if(strcmp(toks[0],"MEILLEUR")==0){

				char* tmp=(strcmp(toks[1],"1")==0)?"Vous avez le meilleur mot ce tour":"vous n'avez pas le meilleur mot ce tour";
				gtk_label_set_text(best_player_label,tmp);
			}
			else{
				printf("Commande inconnue et ignore\n");
			}
		}
		temps2=time(NULL);
		sprintf(timer,"chrono :%ld",temps2-temps1);
		gtk_label_set_text(chrono_label,timer);

	}

	end_connection(sock);
	pthread_exit(NULL);
}

static int init_connection(const char *address)
{
	SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
	SOCKADDR_IN sin = { 0 };
	struct hostent *hostinfo;

	if(sock == INVALID_SOCKET)
	{
		perror("socket()");
		exit(errno);
	}

	hostinfo = gethostbyname(address);
	if (hostinfo == NULL)
	{
		fprintf (stderr, "Unknown host %s.\n", address);
		exit(EXIT_FAILURE);
	}

	sin.sin_addr = *(IN_ADDR *) hostinfo->h_addr;
	sin.sin_port = htons(PORT);
	sin.sin_family = AF_INET;

	if(connect(sock,(SOCKADDR *) &sin, sizeof(SOCKADDR)) == SOCKET_ERROR)
	{
		perror("connect()");
		exit(errno);
	}

	return sock;
}

static void end_connection(int sock)
{
	closesocket(sock);
}

static int read_server(SOCKET sock, char *buffer)
{
	int n = 0;

	if((n = recv(sock, buffer, BUF_SIZE - 1, 0)) < 0)
	{
		perror("recv()");
		exit(errno);
	}

	buffer[n] = 0;


	return n;
}

static void write_server(SOCKET sock, const char *buffer)
{
	if(send(sock, buffer, strlen(buffer), 0) < 0)
	{
		perror("send()");
		exit(errno);
	}
}




int main(int argc, char **argv)
{


	if(argc != 3)
	{
		printf("Usage :./main.x [address] [userName]\n");
		return -1;
	}
	p=initPartie();
	toks=(char**)malloc(5*sizeof(char*));


	gtk_init(&argc,&argv);
	GtkBuilder* build=gtk_builder_new_from_file("mainui.glade");
	GtkWidget* mainWindow=(GtkWidget*) gtk_builder_get_object(build,"MainWindow");
	gtk_builder_connect_signals(build,NULL);
	gtk_widget_show_all(mainWindow);

	user_name=strdup(argv[2]);
	p->user=addUser(p->user,initUser(user_name,"0"));

	sock=init_connection(argv[1]);

	sprintf(buffer,"CONNEXION/%s/\n",p->user->name);

	write_server(sock,buffer);
	int n = read_server(sock, buffer);

/* server down */
	if(n == 0)
	{
		printf("Server disconnected !\n");
		return -1;
	}

	nb_args=string_split(toks,buffer,split);

	if(strcmp(toks[0],"BIENVENUE")==0){
		printf("on a recu Bienvenue %s \n placement %s \n tirage %s \n scores %s \n phase %s \n temps %s\n", p->user->name,toks[1],toks[2],toks[3],toks[4],toks[5]);
		

		GtkTextView* scrabble_text_view=(GtkTextView*) gtk_builder_get_object(build,"scrabble_text_view");
		GtkTextBuffer* scrabble_buffer=gtk_text_view_get_buffer(scrabble_text_view);
		
		set_new_buffer_withmarkup(scrabble_buffer,strdup(toks[1]),strdup(toks[2]));


		setGrille(p->sc,toks[1]);
		setTirage(p->sc,toks[2]);


		GtkLabel* phase_label=(GtkLabel*) gtk_builder_get_object(build,"phase_label");
		sprintf(buffer,"phase : %s",toks[4]);
		gtk_label_set_text(phase_label,buffer);
		p->chrono=strdup(toks[5]);
		char* tmp=strdup(toks[3]);
		nb_args=string_split(toks,tmp,"*");
		p->num_tour=strdup(toks[0]);
		int i;
		for(i=1; i< nb_args;i=i+2){
			p->other_users=addUser(p->other_users,initUser(toks[i],toks[i+1]));
		}

	}else {
		printf("REFUUUUS\n");
		return -3;
	}




	pthread_create(&thread_server,NULL,&app,build);
	
	gtk_main();


	return EXIT_SUCCESS;
}

