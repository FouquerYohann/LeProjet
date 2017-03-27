#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include "parser.h"

#include "client.h"


static void app(const char *address)
{
	SOCKET sock = init_connection(address);
	char buffer[BUF_SIZE];
	char* tok;
	char ** toks=(char**)malloc(sizeof(char*));
	int nb_args=0;
	char* user;
	fd_set rdfs;


	printf("Bonjour, Bienvenue dans le jeu de Scrabble duplicate.\n");
	printf("Quelle est votre nom\n");
	fgets(buffer, BUF_SIZE - 1, stdin);
	user=strdup(buffer);


	while(1)
	{
		FD_ZERO(&rdfs);

/* add STDIN_FILENO */
		FD_SET(STDIN_FILENO, &rdfs);

/* add the socket */
		FD_SET(sock, &rdfs);

		if(select(sock + 1, &rdfs, NULL, NULL, NULL) == -1)
		{
			perror("select()");
			exit(errno);
		}

/* something from standard input : i.e keyboard */
		if(FD_ISSET(STDIN_FILENO, &rdfs))
		{
			nb_args=0;
			printf("pour jouer selectionnez une commande ci dessous\n");
			printf("\t CONNEXION\n");
			printf("\t SORT\n");
			printf("\t TROUVE\n");

			fgets(buffer, BUF_SIZE - 1, stdin);


			if(strcmp(buffer,"SORT")){
				sprintf(buffer,"SORT/%s/",user);
				write_server(sock,buffer);
				break;
			}
			if(strcmp(buffer,"CONNEXION")){
				sprintf(buffer,"SORT/%s/",user);
			}
			if(strcmp(buffer,"TROUVE")){
				scrabble* tmp=readInFic(FIC);
				sprintf(buffer,"TROUVE/%s/",toString(tmp));
			}

			write_server(sock,buffer);

			int n = read_server(sock, buffer);
			
			/* server down */
			if(n == 0)
			{
				printf("Server disconnected !\n");
				break;
			}


			while((tok=strsep(&buffer,"/"))!=NULL){
				free(toks[nb_args]);
				toks[nb_args++]=strdup(tok);
			}

			if(strcmp(toks[0],"BIENVENUE")==0){
				printf("on a recu Bienvenue %s \n", user);
			}
			else if(strcmp(toks[0],"REFUS")==0){
				printf("Refus de la connection \n");
			}
			else if(strcmp(toks[0],"CONNECTE")==0){
				printf("Un nouveau joueur est arrivé : %s\n", toks[1]);
			}
			else if(strcmp(toks[0],"DECONNEXION")==0){
				printf("Le joueur %s c'est deconnecte\n", toks[1]);
			}
			else if(strcmp(toks[0],"SESSION")==0){
				printf("debut d'une nouvelle session\n");
			}
			else if(strcmp(toks[0],"VAINQUEUR")==0){
				printf("la partie est fini. Le bilan %s",toks[1]);
			}
			else if(strcmp(toks[0],"TOUR")==0){
				printf("un nouveau tour. plateau %s tirage %s\n",toks[1],toks[2]);
			}
			else if(strcmp(toks[0],"RVALIDE")==0){
				printf("Placement valide, fin de la phase de recherche\n");
			}
			else if(strcmp(toks[0],"RINVALIDE")==0){
				printf("placement invalide pour la raison %s\n",toks[1]);
			}
			else if(strcmp(toks[0],"RATROUVE")==0){
				printf("le joueur %s a trouve un mot fin de la phase de recherche\n", toks[1]);
			}
			else if(strcmp(toks[0],"RFIN")==0){
				printf("expiration du delai de recherche, fin de la phase de recherche\n");
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




int main(int argc, char **argv)
{
if(argc < 1)
{
printf("Usage : %s [address] \n", argv[0]);
return EXIT_FAILURE;
}

app(argv[1]);


return EXIT_SUCCESS;
}

