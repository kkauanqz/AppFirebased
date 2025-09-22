package com.example.churusbamgos

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.churusbamgos.ui.theme.ChurusbamgosTheme
import com.example.churusbamgos.ui.theme.buttom
import com.example.churusbamgos.ui.theme.fundo
import com.example.churusbamgos.ui.theme.fundon
import com.example.churusbamgos.ui.theme.text
import com.google.firebase.firestore.ktx.firestore

class MainActivity : ComponentActivity() {

    val db = com.google.firebase.ktx.Firebase.firestore//Variável de banco de dados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChurusbamgosTheme {
                val navController =
                    rememberNavController() //Variável reponsável por cuidar da navegação no app
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavHost( //função responsavel pela navegação
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("login") { //Tela de Login
                            TelaLogin(
                                onLogin = { userName ->
                                    navController.navigate("principal/${userName}")//Vai para tela principal, passando um o nome do usuário como parametro
                                },
                                onRegisterClick = {
                                    navController.navigate("cadastro") //Vai para tela de cadastro
                                }
                            )
                        }
                        composable("cadastro") { //Tela de cadastro
                            TelaCadastro(
                                onRegisterComplete = {
                                    navController.navigate("login") //Vai para tela de login
                                },
                                onLoginClick = {
                                    navController.navigate("login") //Vai para tela de login
                                }
                            )
                        }

                        composable(
                            "principal/{userName}", //Tela principal
                            arguments = listOf(navArgument("userName") { //Argumento esperado
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val userName = backStackEntry.arguments?.getString("userName") ?: "" //Pega argumento e guarda em uma variável
                            TelaPrincipal (
                                userName = userName, //Passa variável userName como parametro
                                onLogout = {
                                    navController.navigate("login") { //Volta para o login
                                        popUpTo("home/{userName}") { inclusive = true } //Impede que o usuário volte para tela principal sem fazer login novamente
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    @Composable
    fun TelaLogin(
        onLogin: (String) -> Unit,
        onRegisterClick: () -> Unit
    ){

        var email by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var mostrarSenha by remember { mutableStateOf(false) } //varivel que será utlizada para ocultar ou mostrar a senha no campo
        var errorMessage by remember { mutableStateOf("") } //Varivel que armazena a mensagem de possíveis erros
        val db = com.google.firebase.ktx.Firebase.firestore//Variável de banco de dados

        Column(
            modifier = Modifier //Corpo da tela
                .background(fundo)
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.corvo), //Imagem que fica em cima do formulário
                contentDescription = "Rosa azul",
                modifier = Modifier
                    .size(125.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Login",
                color = text,
                fontFamily = FontFamily.Monospace,
                fontSize = 28.sp
            ) //Título

            if (errorMessage.isNotEmpty()) { //mostra a mensagem de erro, caso tenha alguma
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            TextField( //Campo de email
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", color = Color.Black) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField( //campo de senha
                value = senha,
                onValueChange = { senha = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Senha", color = Color.Black) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(size = 20.dp),
                visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarSenha = !mostrarSenha }) {
                        Icon(
                            painter = painterResource(
                                id = if (mostrarSenha) R.drawable.visivel else R.drawable.invis_vel
                            ),
                            contentDescription = "Toggle password visibility",
                            tint = Color.Gray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Botão de logar
            Button(
                onClick = {
                    if (email.isBlank() || senha.isBlank()) { //caso os  campos de email ou senha estajam vazios, exibe mensagem de erro
                        errorMessage = "Preencha todos os campos"
                        return@Button
                    }

                    db.collection("banco") //Pega as informações no firebase

                        .whereEqualTo("email", email) //verifica se o email bate

                        .whereEqualTo("senha", senha) //verifica se a senha bate

                        .get() //tenta pegar a instancia em que as duas condições batem

                        .addOnSuccessListener { documents -> // Se operação funcionar

                            if (documents.isEmpty) { //Se não houver uma instancia correspondente
                                errorMessage = "Credenciais inválidas"
                            } else { // se houver instancia correspondente
                                val nomeUsuario =
                                    documents.documents[0].getString("apelido") ?: email
                                onLogin(nomeUsuario) //Realiza a função onLogin passando o apelido do usuário como parametro
                            }
                        }
                        .addOnFailureListener { exception -> //Se ocorrer algum erro durante a operação
                            errorMessage = "Erro ao fazer login: ${exception.message}"
                            Log.w("Login", "Erro ao verificar login", exception)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttom
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login", fontSize = 16.sp)
            }

            //Botão que direciona para tela de cadastro
            Button(
                onClick = {
                    onRegisterClick() //Realiza a função onRegisterClick
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = buttom
                ),
                border = BorderStroke(1.dp, buttom),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastre-se", fontSize = 16.sp)
            }
        }
    }

    @Composable
    fun TelaCadastro(
        onRegisterComplete: () -> Unit,
        onLoginClick: () -> Unit
    ){
        var email by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var nome by remember { mutableStateOf("") }
        var apelido by remember { mutableStateOf("") }
        var telefone by remember { mutableStateOf("") }
        var mostrarSenha by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") } //Varivel que armazena a mensagem de possíveis erros


        Column(
            modifier = Modifier //Corpo da tela
                .background(fundon)
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card( //Card que contem o formulário
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(fundo)
            ) {
                Column(
//Formulario
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fundo)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.corvo), //Imagem que fica em cima do formulário
                        contentDescription = "Rosa azul",
                        modifier = Modifier
                            .size(125.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Cadastro",
                        color = text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 28.sp
                    ) //Título

                    Spacer(modifier = Modifier.height(40.dp))

                    TextField( //Campo de nome
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text(text = "Nome", color = Color.Black) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField( //Campo de apelido
                        value = apelido,
                        onValueChange = { apelido = it },
                        label = { Text(text = "Apelido", color = Color.Black) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField( //Campo de email
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email", color = Color.Black) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField( //Campo de senha
                        value = senha,
                        onValueChange = { senha = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Senha", color = Color.Black) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(size = 20.dp),
                        visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { mostrarSenha = !mostrarSenha }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (mostrarSenha) R.drawable.visivel else R.drawable.invis_vel
                                    ),
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField( //Campo de telefone
                        value = telefone,
                        onValueChange = { telefone = it },
                        label = { Text(text = "Telefone", color = Color.Black) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { //Botão de cadastrar
                            if (nome.isBlank() || apelido.isBlank() || email.isBlank() || senha.isBlank()) { //Mensagem de erro caso algum dos campos esteja em branco
                                errorMessage = "Preencha todos os campos obrigatórios"
                                return@Button
                            }

                            val usuario =
                                hashMapOf( //Criar uma variável que contem um hashMap das informações passadas
                                    "nome" to nome,
                                    "apelido" to apelido,
                                    "email" to email,
                                    "senha" to senha,
                                    "telefone" to telefone
                                )

                            db.collection("banco") // Pega as informações armazenadas no firebase
                                .add(usuario) //Tenta adicionar o usuário
                                .addOnSuccessListener { //Se conseguir
                                    Log.d("Firestore", "Documento adicionado com ID: ${it.id}")
                                    onRegisterComplete() //Realiza função onRegisterComplete
                                }
                                .addOnFailureListener { e -> //Caso ocorra um erro no processo
                                    errorMessage = "Erro ao cadastrar: ${e.message}"
                                    Log.w("Firestore", "Erro ao adicionar documento", e)
                                }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttom
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cadastrar", fontSize = 16.sp)
                    }

                    Button(
                        onClick = { onLoginClick() }, //Botão que direciona para tela de login
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fazer login", fontSize = 16.sp)
                    }

                }

            }
        }
    }

    @Composable
    fun TelaPrincipal(
        userName: String = "Usuário",
        onLogout: () -> Unit
    ) {
        var menuAberto by remember { mutableStateOf(false) }
        var mostrarRegistros by remember { mutableStateOf(false) }
        val db = com.google.firebase.ktx.Firebase.firestore
        val banco = remember { mutableStateListOf<Map<String, Any>>() }
        val scrollState = rememberScrollState() // Adicionando estado de scroll

        Column( //Corpo da página
            modifier = Modifier
                .fillMaxSize()
                .background(fundon)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(onClick = { menuAberto = true }) {  // Botão  de menu (três pontos)
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = buttom
                    )
                }

                DropdownMenu( //Menu que é aberto ao clicar nos três pontos
                    expanded = menuAberto,
                    onDismissRequest = { menuAberto = false }
                ) {
                    DropdownMenuItem( //Opção para mostrar os registros
                        text = { Text("Listar Registros") },
                        onClick = {
                            menuAberto = false
                            db.collection("banco") //Pega as informações do firebase
                                .get() //Comando get
                                .addOnSuccessListener { result -> //Se tudo der certo
                                    banco.clear() //limpa a variável banco
                                    for (document in result) { //para cada registro
                                        banco.add(document.data) //Adiciona instancia na variável
                                    }
                                    mostrarRegistros = true
                                }
                                .addOnFailureListener { exception -> //Se algo der errado no processo
                                    Log.w(TAG, "Error getting documents.", exception)
                                }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sair") },
                        onClick = {
                            menuAberto = false
                            onLogout()
                        }
                    )
                }

            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )
            }

            Column( //Coluna scrolavel
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Adicionando scroll aqui
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bem-vindo, $userName!",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 26.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                if (mostrarRegistros) {


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 24.dp,
                                bottom = 40.dp
                            ) // Adicionando padding bottom para espaço
                    ) {
                        banco.forEachIndexed { index, registro ->
                            Column( //Corpo do card de registro
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(
                                        Color.White,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                                    .padding(0.dp, 0.dp, 0.dp, 12.dp)
                            ) {
                                Column( //Coluna com imagem na parte de cima do card
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            fundo,
                                            shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.user),
                                        contentDescription = "Logo",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                                Column { //Coluna que contem as informações
                                    Text("Registro ${index + 1}", color = fundo, fontSize = 18.sp)
                                    Text("Nome: ${registro["nome"]}", color = Color.Black)
                                    Text("Apelido: ${registro["apelido"]}", color = Color.Black)
                                    Text("Email: ${registro["email"]}", color = Color.Black)
                                    Text("Senha: ${registro["senha"]}", color = Color.Black)
                                    Text("Telefone: ${registro["telefone"]}", color = Color.Black)

                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Use o menu no canto superior direito para listar os registros",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun telaLoginPreview(){ //Preview da Tela de Login
        TelaLogin(onLogin = {}, onRegisterClick = {})
    }

    @Preview
    @Composable
    fun telaCadastroPreview(){ //Preview da Tela de Cadastro
        TelaCadastro(onRegisterComplete = {}, onLoginClick = {})
    }

    @Preview
    @Composable
    fun telaPrincipalPreview(){ //Preview da tela principal

        }
}