#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include "parser.h"

#include "client.h"


static void app(const char *address)
{
	scrabble* sc=initScrabble();
	char* score;
	char* user;
	char* chrono;
	char* phase;
	char** other_users;
	int number_users=0;



	SOCKET sock = init_connection(address);
	char buffer[BUF_SIZE];
	char ** toks=(char**)malloc(5*sizeof(char*));
	int nb_args=0;
	const char split[2] = "/";
	fd_set rdfs;


	printf("Bonjour, Bienvenue dans le jeu de Scrabble duplicate.\n");
	printf("Quelle est votre nom\n");
	fgets(buffer, BUF_SIZE - 1, stdin);
	user=strdup(buffer);
	user[strlen(user)-1]='\0';

	sprintf(buffer,"CONNEXION/%s/\n",user);

	write_server(sock,buffer);

	int n = read_server(sock, buffer);

/* server down */
	if(n == 0)
	{
		printf("Server disconnected !\n");
		return;
	}

	nb_args=string_split(toks,buffer,split);

	if(strcmp(toks[0],"BIENVENUE")==0){
		printf("on a recu Bienvenue %s \n placement %s \n tirage %s \n phase %s \n temps %s\n", user,toks[1],toks[2],toks[3],toks[4]);
		sprintf(buffer,"%s%s",toks[1],toks[2]);
		parseScrabble(sc,buffer);
		phase=strdup(toks[3]);
		chrono=strdup(toks[4]);
	}else {
		printf("REFUUUUS\n");
		return;
	}


	while(1)
	{
		FD_ZERO(&rdfs);

		/* add STDIN_FILENO */
		FD_SET(STDIN_FILENO, &rdfs);

		/* add the socket */
		FD_SET(sock, &rdfs);
		print_menu();

		if(select(sock + 1, &rdfs, NULL, NULL, NULL) == -1)
		{
			perror("select()");
			exit(errno);
		}

		if(FD_ISSET(STDIN_FILENO,&rdfs)){
			fgets(buffer, BUF_SIZE - 1, stdin);

			if(strcmp(buffer,"SORT\n")==0){
				sprintf(buffer,"SORT/%s/\n",user);
				write_server(sock,buffer);
				break;
			}
			else if(strcmp(buffer,"TROUVE\n")==0){
				scrabble* tmp=readInFic(FIC);
				sprintf(buffer,"TROUVE/%s/\n",parseOut(tmp));
			}else{
				printf("commande inconnue\n");
				continue;
			}

			write_server(sock,buffer);
		}
		else if(FD_ISSET(sock,&rdfs)){
			int n = read_server(sock, buffer);

			/* server down */
			if(n == 0)
			{
				printf("Server disconnected !\n");
				break;
			}

			nb_args=string_split(toks,buffer,split);


			if(strcmp(toks[0],"CONNECTE")==0){
				printf("Un nouveau joueur est arrivé : %s\n", toks[1]);
				other_users[number_users++]=strdup(toks[1]);
			}
			else if(strcmp(toks[0],"DECONNEXION")==0){
				printf("Le joueur %s s'est deconnecte\n", toks[1]);
				int i=0;
				for (i=0;i<number_users;i++){
					if(strcmp(toks[1],other_users[i])==0){
						int j;
						for(j=i;j<number_users-1;j++){
							free(other_users[j]);
							other_users[j]=strdup(other_users[j+1]);
						}
						free(other_users[number_users--]);
						break;
					}
				}
			}

			else if(strcmp(toks[0],"SESSION")==0){
				printf("debut d'une nouvelle session\n");
				phase=strdup("recherche");
				chrono=strdup("Chrono a IMPLEMENTER");
				score=strdup("0");
				printf("%s\n",toString(sc));
				writeInFic(FIC,sc);
			}
			else if(strcmp(toks[0],"VAINQUEUR")==0){
				printf("la partie est fini. Le bilan %s",toks[1]);
			}
			else if(strcmp(toks[0],"TOUR")==0){
				printf("un nouveau tour. plateau %s tirage %s\n",toks[1],toks[2]);
				sprintf(buffer,"%s%s",toks[1],toks[2]);
				parseScrabble(sc,buffer);
				printf("%s\n", toString(sc));
				writeInFic(FIC,sc);
			}
			else if(strcmp(toks[0],"RVALIDE")==0){
				printf("Placement valide, fin de la phase de recherche\n");
				free(phase);
				phase=strdup("SOUMISSION");
				free(chrono);
				strdup("CHRONO A IMPLEMENTER");
			}
			else if(strcmp(toks[0],"RINVALIDE")==0){
				printf("placement invalide pour la raison %s\n",toks[1]);
				writeInFic(FIC,sc);
			}
			else if(strcmp(toks[0],"RATROUVE")==0){
				printf("le joueur %s a trouve un mot fin de la phase de recherche\n", toks[1]);
			}
			else if(strcmp(toks[0],"RFIN")==0){
				printf("expiration du delai de recherche, fin de la phase de recherche\n");
				free(chrono);
				strdup("CHRONO A IMPLEMENTER");
			}
			else if(strcmp(toks[0],"SVALIDE")==0){
				printf("soumission valide\n");
			}
			else if(strcmp(toks[0],"SINVALIDE")==0){
				printf("soumission invalide pour la raison %s\n",toks[1] );
			}
			else if(strcmp(toks[0],"SFIN")==0){
				printf("expiration du delai de soumission fin de la phase de soumission\n");
			}	
			else if(strcmp(toks[0],"BILAN")==0){
				printf("bilan du tour mot %s par %s scores pour tout les joueur %s\n",toks[1],toks[2],toks[3] );
			}
			else{
				printf("Commande inconnue et ignore\n");
			}
		}


	}

	end_connection(sock);
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

void print_menu(){
	printf("pour jouer selectionnez une commande ci dessous\n");
	printf("\t SORT\n");
	printf("\t TROUVE\n");
}

int main(int argc, char **argv)
{
	if(argc == 1)
	{
		printf("Usage : %s [address] \n", argv[0]);
		printf("Utilisation de localhost\n");
		argv[1]="localhost";
	}

	app(argv[1]);


	return EXIT_SUCCESS;
}

