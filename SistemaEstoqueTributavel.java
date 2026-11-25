import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SistemaEstoqueTributavel {

    public static int lerInteiro(Scanner entrada, String mensagem) {
        int valor = 0;
        boolean lido = false;
        while (!lido) {
            System.out.print(mensagem);
            try {
                valor = entrada.nextInt();
                String resto = entrada.nextLine(); 
                if (resto.equals("")) {
                    lido = true;
                } else {
                    System.out.println("[ERRO] Digite só o número, sem nada depois.");
                }
            } catch (Exception e) {
                entrada.nextLine(); 
                System.out.println("[ERRO] Digite apenas números inteiros.");
            }
        }
        return valor;
    }

    public static double lerDuasPartes(Scanner entrada, String mensagemInteira, String mensagemFracao) {
        int inteira = -1;
        while (inteira < 0) {
            inteira = lerInteiro(entrada, mensagemInteira);
            if (inteira < 0) {
                System.out.println("[ERRO] Não pode ser negativo.");
            }
        }
        int fracao = -1;
        while (fracao < 0 || fracao > 99) {
            fracao = lerInteiro(entrada, mensagemFracao);
            if (fracao < 0 || fracao > 99) {
                System.out.println("[ERRO] Digite um número de 0 a 99.");
            }
        }
        return inteira + fracao / 100.0;
    }

    public static double arredondarInteiro(double numero) {
        double x = numero + 0.5;
        double parteDecimal = x % 1;
        double inteiro = x - parteDecimal;
        if (parteDecimal < 0) {
            inteiro = inteiro - 1;     
        }
        return inteiro;
    }

    public static double arredondar(double valor) {
        return arredondarInteiro(valor * 100) / 100.0;
    }

    public static String formatoQtd(boolean porMetro) {
        if (porMetro) {
            return "%.2f m";
        }
        return "%.0f un";
    }

    public static double calcularSubtotal(double quantidade, double precoUnitario) {
        return arredondar(quantidade * precoUnitario);
    }

    public static double calcularImposto(double valor, double aliquota) {
        return arredondar(valor * aliquota);
    }

    public static double calcularTotal(double subtotal, double imposto) {
        return arredondar(subtotal + imposto);
    }

    public static String situacaoEstoque(double estoque, double minimo) {
        if (estoque <= 0) {
            return "ESGOTADO";
        } else if (estoque <= minimo) {
            return "REPOSIÇÃO";
        }
        return "OK";
    }

    public static double estoqueAposReposicao(double atual, double quantidade, int maximo) {
        if (!(quantidade > 0)) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (!(atual + quantidade <= maximo)) {
            throw new IllegalArgumentException("O estoque não pode passar de " + maximo + ".");
        }
        return arredondar(atual + quantidade);
    }

    public static boolean precisaRepor(double estoque, double minimo) {
        return estoque <= minimo;
    }

    public static boolean quantidadeValida(double quantidade, double estoque) {
        return quantidade > 0 && quantidade <= estoque;  
    }

    public static boolean precoValido(double preco, double precoMaximo) {
        return preco >= 0.01 && preco <= precoMaximo;    
    }

    public static boolean skuExiste(String[] skus, int total, String sku) {
        for (int i = 0; i < total; i++) {
            if (skus[i].equals(sku)) {
                return true;
            }
        }
        return false;
    }

    public static double metragemDeCabos(double unidades, double comprimento) {
        return arredondar(unidades * comprimento);
    }

    public static double percentualRestante(double atual, double inicial) {
        if (inicial <= 0) {
            return 0;
        }
        return atual / inicial * 100;
    }

    public static int buscarUsuario(ArrayList<String> logins, ArrayList<String> apelidos, String usuario) {
        for (int i = 0; i < logins.size(); i++) {
            if (logins.get(i).equals(usuario) || apelidos.get(i).equals(usuario)) {
                return i;
            }
        }
        return -1;
    }

    public static void validarNovoLogin(ArrayList<String> logins, ArrayList<String> apelidos, String login) {
        if (login.equals("SAIR") || login.equals("sair")) {
            throw new IllegalArgumentException("SAIR é reservado para encerrar o sistema.");
        }
        if (buscarUsuario(logins, apelidos, login) != -1) {
            throw new IllegalArgumentException("Já existe um usuário com esse login.");
        }
    }

    public static String nomePerfil(int codigo) {
        switch (codigo) {
            case 1:
                return "ADMINISTRADOR";
            case 2:
                return "VENDEDOR";
            default:
                return "";
        }
    }

    public static String formaPagamento(int codigo) {
        switch (codigo) {
            case 1:
                return "Pix";
            case 2:
                return "Cartão de débito";
            case 3:
                return "Cartão de crédito";
            case 4:
                return "Boleto";
            default:
                return "";
        }
    }

    public static double mostrarOrcamento(int numero, ArrayList<Integer> itemOrcamento, ArrayList<Integer> itemProduto,
            ArrayList<Double> itemQtd, String[] especificacoes, double[] precos, boolean[] porMetro, int regime, String uf) {
        double aliquota = aliquotaTotal(regime, aliquotaIcms(uf));
        double total = 0;
        for (int i = 0; i < itemOrcamento.size(); i++) {
            if (itemOrcamento.get(i) == numero) {
                int p = itemProduto.get(i);
                double subtotal = calcularSubtotal(itemQtd.get(i), precos[p]);
                double valor = calcularTotal(subtotal, calcularImposto(subtotal, aliquota));
                System.out.printf("   - " + formatoQtd(porMetro[p]) + " de %s | R$ %.2f com impostos%n",
                    itemQtd.get(i), especificacoes[p], valor);
                total = arredondar(total + valor);
            }
        }
        System.out.printf("   Total: R$ %.2f - destino %s, %s%n", total, uf, nomeRegime(regime));
        return total;
    }

    public static String ufMaiuscula(String uf) {
        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
            "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        String[] ufsMinusculas = {"ac", "al", "ap", "am", "ba", "ce", "df", "es", "go", "ma", "mt", "ms", "mg",
            "pa", "pb", "pr", "pe", "pi", "rj", "rn", "rs", "ro", "rr", "sc", "sp", "se", "to"};
        for (int i = 0; i < ufs.length; i++) {
            if (ufs[i].equals(uf) || ufsMinusculas[i].equals(uf)) {
                return ufs[i];
            }
        }
        return "";
    }

    public static double aliquotaIcms(String uf) {
        if (uf.equals("SP")) {
            return 0.18;
        }
        return 0.12;
    }

    public static double aliquotaTotal(int regime, double icms) {
        switch (regime) {
            case 1:
                return 0.04;                       
            case 2:
                return icms + 0.0065 + 0.03;       
            default:
                return icms + 0.0165 + 0.076;      
        }
    }

    public static String nomeRegime(int regime) {
        switch (regime) {
            case 1:
                return "Simples Nacional";
            case 2:
                return "Lucro Presumido";
            default:
                return "Lucro Real";
        }
    }

    public static String descricaoTributos(int regime, String uf) {
        String icms;
        if (uf.equals("SP")) {
            icms = "ICMS 18%";
        } else {
            icms = "ICMS 12%";
        }
        switch (regime) {
            case 1:
                return "DAS 4%";
            case 2:
                return icms + " + PIS 0,65% + COFINS 3%";
            default:
                return icms + " + PIS 1,65% + COFINS 7,6%";
        }
    }

    public static String tipoDocumento(String uf, boolean balcao) {
        if (uf.equals("SP") && balcao) {
            return "NFC-e";
        }
        return "NF-e";
    }

    public static boolean ncmValido(int ncm) {
        return ncm >= 1 && ncm <= 99999999;
    }

    public static boolean cestValido(int cest) {
        return cest >= 1 && cest <= 9999999;
    }

    public static void imprimirNcmCest(int ncm, int cest) {
        System.out.printf("NCM: %04d.%02d.%02d | CEST: %02d.%03d.%02d",
            ncm / 10000, ncm / 100 % 100, ncm % 100,
            cest / 100000, cest / 100 % 1000, cest % 100);
    }

    public static boolean mesValido(int mes) {
        return mes >= 1 && mes <= 12;
    }

    public static boolean respostaValida(String resposta) {
        return resposta.equals("S") || resposta.equals("s") || resposta.equals("N") || resposta.equals("n");
    }

    public static boolean respostaSim(String resposta) {
        return resposta.equals("S") || resposta.equals("s");
    }

    public static double participacao(double valor, double total) {
        if (total <= 0) {
            return 0;
        }
        return valor / total * 100;
    }

    public static double percentualGiro(double vendido, double estoqueAtual) {
        return participacao(vendido, vendido + estoqueAtual);
    }

    public static String classeABC(double acumuladoAntes) {
        if (acumuladoAntes < 80) {
            return "A";
        } else if (acumuladoAntes < 95) {
            return "B";
        }
        return "C";
    }

    public static double centavos(double valor) {
        return arredondarInteiro(valor * 100);
    }

    public static String textoInteiro(double numero) {
        String texto = "";
        do {
            double ultimo = numero % 10;
            for (int algarismo = 0; algarismo <= 9; algarismo++) {
                if (ultimo == algarismo) {
                    texto = algarismo + texto;
                }
            }
            numero = (numero - ultimo) / 10;
        } while (numero > 0);
        return texto;
    }

    public static String formatarDuasCasas(double valor) {
        double total = centavos(valor);
        double resto = total % 100;
        String casas = textoInteiro(resto);
        if (resto < 10) {
            casas = "0" + casas;
        }
        return textoInteiro((total - resto) / 100) + "," + casas;
    }

    public static void gravarLinha(BufferedWriter arquivo, String texto) throws IOException {
        arquivo.write(texto);
        arquivo.newLine();
    }

    public static void gravarValor(BufferedWriter arquivo, double valor) throws IOException {
        double total = centavos(valor);
        double resto = total % 1000000;
        gravarLinha(arquivo, textoInteiro((total - resto) / 1000000));
        gravarLinha(arquivo, textoInteiro(resto));
    }

    public static int lerInteiroArquivo(Scanner leitor) {
        int valor = leitor.nextInt();
        leitor.nextLine();
        return valor;
    }

    public static double lerValorArquivo(Scanner leitor) {
        int milhoes = lerInteiroArquivo(leitor);
        int resto = lerInteiroArquivo(leitor);
        return (milhoes * 1000000.0 + resto) / 100.0;
    }

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        int capacidade = 30;
        String[] skus = new String[capacidade];
        String[] categorias = new String[capacidade];
        String[] especificacoes = new String[capacidade];
        double[] precos = new double[capacidade];         
        double[] estoques = new double[capacidade];        
        double[] estoquesMinimos = new double[capacidade];
        boolean[] porMetro = new boolean[capacidade];      
        double[] comprimentos = new double[capacidade];    
        double[] vendidos = new double[capacidade];
        int[] ncms = new int[capacidade];                 
        int[] cests = new int[capacidade];                

        String[] listaCategorias = {"HDMI", "USB", "USB-C", "Micro-USB", "Rede", "SATA",
            "Fibra Óptica", "Coaxial", "RCA", "XLR"};

        String arquivoDados = "estoque_dados.txt";
        String arquivoRelatorio = "relatorio_contabil.txt";

        double precoMaximo = 1000000.0;
        int quantidadeMaxima = 100000;
        int regime = 3; 

        skus[0] = "HDMI-2.1-001"; categorias[0] = "HDMI"; especificacoes[0] = "HDMI 2.1 - 2m"; precos[0] = 100.0; estoques[0] = 50; estoquesMinimos[0] = 10; comprimentos[0] = 2.0;
        skus[1] = "HDMI-1.4-011"; categorias[1] = "HDMI"; especificacoes[1] = "HDMI 1.4 - 1.5m"; precos[1] = 30.0; estoques[1] = 40; estoquesMinimos[1] = 10; comprimentos[1] = 1.5;
        skus[2] = "HDMI-MINI-013"; categorias[2] = "HDMI"; especificacoes[2] = "HDMI Mini - 1m"; precos[2] = 45.0; estoques[2] = 6; estoquesMinimos[2] = 10; comprimentos[2] = 1.0;
        skus[3] = "USB-A-3.0-002"; categorias[3] = "USB"; especificacoes[3] = "USB 3.0 - 1m"; precos[3] = 35.0; estoques[3] = 40; estoquesMinimos[3] = 10; comprimentos[3] = 1.0;
        skus[4] = "USB-EXT-014"; categorias[4] = "USB"; especificacoes[4] = "USB Extensor - 2m"; precos[4] = 20.0; estoques[4] = 50; estoquesMinimos[4] = 10; comprimentos[4] = 2.0;
        skus[5] = "USB-HUB-016"; categorias[5] = "USB"; especificacoes[5] = "USB Hub 4 Portas"; precos[5] = 38.0; estoques[5] = 7; estoquesMinimos[5] = 10;
        skus[6] = "RED-CAT6-003"; categorias[6] = "Rede"; especificacoes[6] = "Cat6 - 5m"; precos[6] = 25.0; estoques[6] = 60; estoquesMinimos[6] = 10; comprimentos[6] = 5.0;
        skus[7] = "RED-CAT5E-017"; categorias[7] = "Rede"; especificacoes[7] = "Cat5e - 3m"; precos[7] = 12.0; estoques[7] = 80; estoquesMinimos[7] = 10; comprimentos[7] = 3.0;
        skus[8] = "SATA-3.0-004"; categorias[8] = "SATA"; especificacoes[8] = "SATA III - 50cm"; precos[8] = 35.0; estoques[8] = 8; estoquesMinimos[8] = 10; comprimentos[8] = 0.5;
        skus[9] = "SATA-ESATA-022"; categorias[9] = "SATA"; especificacoes[9] = "Cabo eSATA"; precos[9] = 42.0; estoques[9] = 5; estoquesMinimos[9] = 10;
        skus[10] = "USBC-100W-005"; categorias[10] = "USB-C"; especificacoes[10] = "Cabo USB-C 100W"; precos[10] = 45.0; estoques[10] = 45; estoquesMinimos[10] = 10;
        skus[11] = "FIB-LC-006"; categorias[11] = "Fibra Óptica"; especificacoes[11] = "Fibra LC-LC"; precos[11] = 75.0; estoques[11] = 25; estoquesMinimos[11] = 10;
        skus[12] = "COX-RG6-007"; categorias[12] = "Coaxial"; especificacoes[12] = "Cabo Coaxial RG6"; precos[12] = 18.0; estoques[12] = 70; estoquesMinimos[12] = 10;
        skus[13] = "RCA-AUDIO-008"; categorias[13] = "RCA"; especificacoes[13] = "Cabo RCA Áudio"; precos[13] = 22.0; estoques[13] = 30; estoquesMinimos[13] = 10;
        skus[14] = "RCA-3AV-032"; categorias[14] = "RCA"; especificacoes[14] = "Cabo 3x3 RCA Vídeo/Áudio"; precos[14] = 18.0; estoques[14] = 55; estoquesMinimos[14] = 10;
        skus[15] = "XLR-MIC-009"; categorias[15] = "XLR"; especificacoes[15] = "Cabo Microfone XLR"; precos[15] = 65.0; estoques[15] = 5; estoquesMinimos[15] = 10;
        skus[16] = "XLR-BAL-036"; categorias[16] = "XLR"; especificacoes[16] = "Cabo XLR Balanceado"; precos[16] = 115.0; estoques[16] = 11; estoquesMinimos[16] = 10;
        skus[17] = "MICUSB-V8-010"; categorias[17] = "Micro-USB"; especificacoes[17] = "Cabo Micro-USB V8"; precos[17] = 15.0; estoques[17] = 65; estoquesMinimos[17] = 10;
        skus[18] = "RED-CAT6-ROLO"; categorias[18] = "Rede"; especificacoes[18] = "Rolo Cat6 - caixa 305m"; precos[18] = 3.50; estoques[18] = 305; estoquesMinimos[18] = 50; porMetro[18] = true;
        skus[19] = "RED-CAT5E-ROLO"; categorias[19] = "Rede"; especificacoes[19] = "Rolo Cat5e - caixa 305m"; precos[19] = 2.20; estoques[19] = 305; estoquesMinimos[19] = 50; porMetro[19] = true;
        skus[20] = "COX-RG6-ROLO"; categorias[20] = "Coaxial"; especificacoes[20] = "Rolo Coaxial RG6 - 100m"; precos[20] = 2.80; estoques[20] = 100; estoquesMinimos[20] = 20; porMetro[20] = true;

        int totalProdutos = 21;

        for (int i = 0; i < totalProdutos; i++) {
            ncms[i] = 85444200;   
            cests[i] = 1200100;  
        }
        ncms[5] = 84718000;   
        ncms[11] = 85447010;  
        ncms[12] = 85442000;  
        ncms[18] = 85444900;  
        ncms[19] = 85444900;  
        ncms[20] = 85442000;  

        ArrayList<Double> historicoQtd = new ArrayList<>();
        ArrayList<Double> historicoValores = new ArrayList<>();
        ArrayList<Double> historicoImpostos = new ArrayList<>();
        ArrayList<Integer> historicoProduto = new ArrayList<>();
        ArrayList<Integer> historicoMes = new ArrayList<>();
        ArrayList<String> historicoUf = new ArrayList<>();
        ArrayList<String> historicoDocumento = new ArrayList<>();
        ArrayList<String> historicoVendedor = new ArrayList<>();   
        ArrayList<Integer> historicoCliente = new ArrayList<>();   
        ArrayList<String> historicoPagamento = new ArrayList<>(); 
        double totalVendidoGeral = 0;
        double totalImpostosGeral = 0;
        int numeroNfce = 0;
        int numeroNfe = 0;

        ArrayList<String> usuarioLogin = new ArrayList<>();
        ArrayList<String> usuarioApelido = new ArrayList<>();      
        ArrayList<String> usuarioSenha = new ArrayList<>();
        ArrayList<String> usuarioPerfil = new ArrayList<>();       
        ArrayList<Boolean> usuarioAtivo = new ArrayList<>();       
        ArrayList<Integer> usuarioCliente = new ArrayList<>();     
        ArrayList<String> clienteNome = new ArrayList<>();
        ArrayList<String> clienteDocumento = new ArrayList<>();    
        ArrayList<Integer> orcamentoCliente = new ArrayList<>();
        ArrayList<String> orcamentoVendedor = new ArrayList<>();
        ArrayList<String> orcamentoUf = new ArrayList<>();
        ArrayList<String> orcamentoStatus = new ArrayList<>();     
        ArrayList<Integer> itemOrcamento = new ArrayList<>();      
        ArrayList<Integer> itemProduto = new ArrayList<>();
        ArrayList<Double> itemQtd = new ArrayList<>();

        usuarioLogin.add("ADMIN");
        usuarioApelido.add("admin");
        usuarioSenha.add("123");
        usuarioPerfil.add("ADMINISTRADOR");
        usuarioAtivo.add(true);
        usuarioCliente.add(-1);

        usuarioLogin.add("VENDEDOR");
        usuarioApelido.add("vendedor");
        usuarioSenha.add("456");
        usuarioPerfil.add("VENDEDOR");
        usuarioAtivo.add(true);
        usuarioCliente.add(-1);

        clienteNome.add("Maria Souza");
        clienteDocumento.add("12345678900");
        usuarioLogin.add("12345678900");          
        usuarioApelido.add("12345678900");
        usuarioSenha.add("789");
        usuarioPerfil.add("CLIENTE");
        usuarioAtivo.add(true);
        usuarioCliente.add(0);

        boolean sistemaAtivo = true;

        try {
            while (sistemaAtivo) {
                boolean logado = false;
                String perfilAtual = "";
                String loginAtual = "";
                int clienteAtual = -1;

                while (!logado && sistemaAtivo) {
                    System.out.println("==================================");
                    System.out.println("   LOGIN - SISTEMA DE ESTOQUE     ");
                    System.out.println("==================================");
                    System.out.println("Opções de acesso:");
                    System.out.println(" - ADMIN");
                    System.out.println(" - VENDEDOR");
                    System.out.println(" - CLIENTE (CPF/CNPJ cadastrado)");
                    System.out.println(" - Digite SAIR para encerrar o sistema");
                    System.out.println("----------------------------------");

                    System.out.print("Digite o usuário: ");
                    String usuario = entrada.nextLine();

                    if (usuario.equals("SAIR") || usuario.equals("sair")) {
                        sistemaAtivo = false;
                    } else {
                        System.out.print("Digite a senha: ");
                        String senha = entrada.nextLine();

                        int indiceUsuario = buscarUsuario(usuarioLogin, usuarioApelido, usuario);
                        if (indiceUsuario == -1 || !usuarioSenha.get(indiceUsuario).equals(senha)) {
                            System.out.println("[ERRO] Usuário ou senha incorretos. Tente novamente.");
                        } else if (!usuarioAtivo.get(indiceUsuario)) {
                            System.out.println("[ERRO] Este usuário está bloqueado. Procure o administrador.");
                        } else {
                            logado = true;
                            perfilAtual = usuarioPerfil.get(indiceUsuario);
                            loginAtual = usuarioLogin.get(indiceUsuario);
                            clienteAtual = usuarioCliente.get(indiceUsuario);
                        }
                    }
                }

                if (logado) {
                    if (perfilAtual.equals("CLIENTE")) {
                        System.out.println("Login efetuado com sucesso! Olá, " + clienteNome.get(clienteAtual) + ".");
                    } else {
                        System.out.println("Login efetuado com sucesso! Bem-vindo, " + perfilAtual + ".");
                    }
                    int opcao = 0;

                    if (perfilAtual.equals("ADMINISTRADOR")) {

                        int qtdAlertas = 0;
                        for (int i = 0; i < totalProdutos; i++) {
                            if (precisaRepor(estoques[i], estoquesMinimos[i])) {
                                if (qtdAlertas == 0) {
                                    System.out.println("ALERTA DE ESTOQUE: produtos que precisam de reposição");
                                }
                                String situacao;
                                if (situacaoEstoque(estoques[i], estoquesMinimos[i]).equals("ESGOTADO")) {
                                    situacao = "ESGOTADO";
                                } else {
                                    situacao = "BAIXO";
                                }
                                System.out.printf("- %s | %s | Restam: " + formatoQtd(porMetro[i]) + " | mínimo: "
                                    + formatoQtd(porMetro[i]) + " | %s%n",
                                    skus[i], especificacoes[i], estoques[i], estoquesMinimos[i], situacao);
                                qtdAlertas++;
                            }
                        }
                        if (qtdAlertas == 0) {
                            System.out.println("Nenhum produto abaixo do estoque mínimo.");
                        } else {
                            System.out.println("Total de produtos para repor: " + qtdAlertas);
                        }

                        do {
                            System.out.println("--- MENU ADMINISTRADOR ---");
                            System.out.println("1. Visão Geral do Estoque");
                            System.out.println("2. Estoque Detalhado e Tributação");
                            System.out.println("3. Cadastrar Novo Produto");
                            System.out.println("4. Relatório de Vendas");
                            System.out.println("5. Metragem em Estoque");
                            System.out.println("6. Fiscal e Relatórios: regime, mês, categoria, curva ABC");
                            System.out.println("7. Repor Estoque");
                            System.out.println("8. Arquivos: salvar, carregar e exportar relatório");
                            System.out.println("9. Usuários e permissões");
                            System.out.println("10. Sair / Deslogar");
                            opcao = lerInteiro(entrada, "Escolha uma opção: ");

                            switch (opcao) {
                                case 1: 
                                    System.out.println("--- VISÃO GERAL DO ESTOQUE ---");
                                    double totalUnidades = 0;
                                    double totalMetrosRolo = 0;
                                    int produtosBaixos = 0;
                                    int produtosEsgotados = 0;

                                    for (int i = 0; i < totalProdutos; i++) {
                                        if (porMetro[i]) {
                                            totalMetrosRolo += estoques[i];
                                        } else {
                                            totalUnidades += estoques[i];
                                        }
                                        String situacaoItem = situacaoEstoque(estoques[i], estoquesMinimos[i]);
                                        if (situacaoItem.equals("ESGOTADO")) {
                                            produtosEsgotados++;
                                        } else if (situacaoItem.equals("REPOSIÇÃO")) {
                                            produtosBaixos++;
                                        }
                                    }

                                    System.out.println("Total de tipos de produtos: " + totalProdutos);
                                    System.out.printf("Total de unidades físicas em cabos prontos: %.0f%n", totalUnidades);
                                    System.out.printf("Total de metros em rolos: %.2f m%n", totalMetrosRolo);
                                    System.out.println("Produtos com estoque no mínimo ou abaixo: " + produtosBaixos);
                                    System.out.println("Produtos esgotados: " + produtosEsgotados);

                                    System.out.println("Resumo por produto:");
                                    for (int i = 0; i < totalProdutos; i++) {
                                        System.out.printf("- %s | Estoque: " + formatoQtd(porMetro[i]) + " | Status: %s%n",
                                            especificacoes[i], estoques[i], situacaoEstoque(estoques[i], estoquesMinimos[i]));
                                    }
                                    break;

                                case 2: 
                                    System.out.println("--- ESTOQUE DETALHADO E TRIBUTÁVEL ---");
                                    double aliquotaInterna = aliquotaTotal(regime, aliquotaIcms("SP"));
                                    System.out.printf("Regime: %s | Impostos de venda interna em SP: %s = %.2f%%%n",
                                        nomeRegime(regime), descricaoTributos(regime, "SP"), aliquotaInterna * 100);
                                    double totalGeralEstoque = 0;

                                    for (int i = 0; i < totalProdutos; i++) {
                                        double precoBase = precos[i];
                                        double imposto = calcularImposto(precoBase, aliquotaInterna);
                                        double precoTotalUnidade = calcularTotal(precoBase, imposto);
                                        double valorTotalItem = calcularSubtotal(estoques[i], precoTotalUnidade);
                                        totalGeralEstoque = arredondar(totalGeralEstoque + valorTotalItem);

                                        String medida;
                                        if (porMetro[i]) {
                                            medida = "m";
                                        } else {
                                            medida = "un";
                                        }

                                        System.out.printf("Código: %s | Tipo: %s | Qtd: " + formatoQtd(porMetro[i])
                                            + " | Preço Base: R$ %.2f/%s | Imposto: R$ %.2f | Preço Total: R$ %.2f/%s | Total Geral: R$ %.2f | Status: %s | ",
                                            skus[i], categorias[i], estoques[i], precoBase, medida, imposto, precoTotalUnidade,
                                            medida, valorTotalItem, situacaoEstoque(estoques[i], estoquesMinimos[i]));
                                        imprimirNcmCest(ncms[i], cests[i]);
                                        System.out.println();
                                    }
                                    System.out.printf("TOTAL GERAL DO ESTOQUE: R$ %.2f%n", totalGeralEstoque);
                                    break;

                                case 3: 
                                    if (totalProdutos >= capacidade) {
                                        System.out.println("[ERRO] Capacidade máxima de cadastro atingida.");
                                    } else {
                                        System.out.println("--- CADASTRAR NOVO PRODUTO ---");

                                        String novoSku = "";
                                        boolean skuValido = false;
                                        while (!skuValido) {
                                            System.out.print("Digite o código SKU, sem espaços: ");
                                            novoSku = entrada.next();
                                            entrada.nextLine(); 
                                            if (skuExiste(skus, totalProdutos, novoSku)) {
                                                System.out.println("[ERRO] Já existe um produto com esse código.");
                                            } else {
                                                skuValido = true;
                                            }
                                        }

                                        System.out.println("Categorias:");
                                        for (int c = 0; c < listaCategorias.length; c++) {
                                            System.out.println("  " + (c + 1) + " = " + listaCategorias[c]);
                                        }
                                        int escolhaCategoria = 0;
                                        while (escolhaCategoria < 1 || escolhaCategoria > listaCategorias.length) {
                                            escolhaCategoria = lerInteiro(entrada, "Escolha a categoria: ");
                                            if (escolhaCategoria < 1 || escolhaCategoria > listaCategorias.length) {
                                                System.out.println("[ERRO] Digite um número de 1 a " + listaCategorias.length + ".");
                                            }
                                        }
                                        String novaCategoria = listaCategorias[escolhaCategoria - 1];

                                        String novaEspecificacao = "";
                                        while (novaEspecificacao.equals("")) {
                                            System.out.print("Digite a especificação/nome: ");
                                            novaEspecificacao = entrada.nextLine();
                                            if (novaEspecificacao.equals("")) {
                                                System.out.println("[ERRO] A especificação não pode ficar vazia.");
                                            }
                                        }

                                        int novoNcm = 0;
                                        while (!ncmValido(novoNcm)) {
                                            novoNcm = lerInteiro(entrada, "Digite o NCM, só os 8 números, ex.: 85444200: ");
                                            if (!ncmValido(novoNcm)) {
                                                System.out.println("[ERRO] O NCM deve ter até 8 dígitos e ser maior que zero.");
                                            }
                                        }
                                        int novoCest = 0;
                                        while (!cestValido(novoCest)) {
                                            novoCest = lerInteiro(entrada, "Digite o CEST, só os 7 números, ex.: 1200100: ");
                                            if (!cestValido(novoCest)) {
                                                System.out.println("[ERRO] O CEST deve ter até 7 dígitos e ser maior que zero.");
                                            }
                                        }

                                        int formaVenda = 0;
                                        while (formaVenda != 1 && formaVenda != 2) {
                                            formaVenda = lerInteiro(entrada, "Forma de venda - 1 = por unidade | 2 = por metro/rolo: ");
                                            if (formaVenda != 1 && formaVenda != 2) {
                                                System.out.println("[ERRO] Digite 1 ou 2.");
                                            }
                                        }
                                        boolean novoPorMetro = formaVenda == 2;
                                        String medidaPreco;
                                        if (novoPorMetro) {
                                            medidaPreco = "metro";
                                        } else {
                                            medidaPreco = "unidade";
                                        }

                                        double novoPreco = 0;
                                        while (!precoValido(novoPreco, precoMaximo)) {
                                            System.out.println("Preço base por " + medidaPreco + " - ex.: R$ 49,90 = 49 reais e 90 centavos");
                                            novoPreco = lerDuasPartes(entrada, "  Reais: ", "  Centavos, de 0 a 99: ");
                                            if (!precoValido(novoPreco, precoMaximo)) {
                                                System.out.println("[ERRO] O preço deve ser de pelo menos R$ 0,01 e até R$ 1.000.000,00.");
                                            }
                                        }

                                        double novoEstoque = -1;
                                        double novoMinimo = -1;
                                        double novoComprimento = -1;

                                        if (novoPorMetro) {
                                            while (novoEstoque < 0 || novoEstoque > quantidadeMaxima) {
                                                System.out.println("Metragem inicial do rolo - ex.: 305,50 m = 305 metros e 50 centímetros");
                                                novoEstoque = lerDuasPartes(entrada, "  Metros: ", "  Centímetros, de 0 a 99: ");
                                                if (novoEstoque < 0 || novoEstoque > quantidadeMaxima) {
                                                    System.out.println("[ERRO] A metragem deve estar entre 0 e " + quantidadeMaxima + " m.");
                                                }
                                            }
                                            while (novoMinimo < 0 || novoMinimo > quantidadeMaxima) {
                                                System.out.println("Metragem mínima para alerta");
                                                novoMinimo = lerDuasPartes(entrada, "  Metros: ", "  Centímetros, de 0 a 99: ");
                                                if (novoMinimo < 0 || novoMinimo > quantidadeMaxima) {
                                                    System.out.println("[ERRO] O mínimo deve estar entre 0 e " + quantidadeMaxima + " m.");
                                                }
                                            }
                                            novoEstoque = arredondar(novoEstoque);
                                            novoMinimo = arredondar(novoMinimo);
                                            novoComprimento = 0;
                                        } else {
                                            while (novoEstoque < 0 || novoEstoque > quantidadeMaxima) {
                                                novoEstoque = lerInteiro(entrada, "Digite a quantidade inicial: ");
                                                if (novoEstoque < 0 || novoEstoque > quantidadeMaxima) {
                                                    System.out.println("[ERRO] A quantidade deve estar entre 0 e " + quantidadeMaxima + ".");
                                                }
                                            }
                                            while (novoMinimo < 0 || novoMinimo > quantidadeMaxima) {
                                                novoMinimo = lerInteiro(entrada, "Digite o limite de estoque mínimo para alerta: ");
                                                if (novoMinimo < 0 || novoMinimo > quantidadeMaxima) {
                                                    System.out.println("[ERRO] O mínimo deve estar entre 0 e " + quantidadeMaxima + ".");
                                                }
                                            }
                                            while (novoComprimento < 0 || novoComprimento > 1000) {
                                                System.out.println("Comprimento de cada cabo - 0 metros e 0 centímetros se não se aplica");
                                                novoComprimento = lerDuasPartes(entrada, "  Metros: ", "  Centímetros, de 0 a 99: ");
                                                if (novoComprimento < 0 || novoComprimento > 1000) {
                                                    System.out.println("[ERRO] O comprimento deve estar entre 0 e 1000 m.");
                                                }
                                            }
                                        }

                                        skus[totalProdutos] = novoSku;
                                        categorias[totalProdutos] = novaCategoria;
                                        especificacoes[totalProdutos] = novaEspecificacao;
                                        precos[totalProdutos] = novoPreco;
                                        estoques[totalProdutos] = novoEstoque;
                                        estoquesMinimos[totalProdutos] = novoMinimo;
                                        porMetro[totalProdutos] = novoPorMetro;
                                        comprimentos[totalProdutos] = novoComprimento;
                                        ncms[totalProdutos] = novoNcm;
                                        cests[totalProdutos] = novoCest;
                                        totalProdutos++;
                                        System.out.println("Produto cadastrado com sucesso e integrado ao estoque geral!");
                                    }
                                    break;

                                case 4:  
                                    System.out.println("--- RELATÓRIO DE VENDAS ---");
                                    if (historicoQtd.size() == 0) {
                                        System.out.println("Nenhuma venda realizada até o momento.");
                                    } else {
                                        for (int i = 0; i < historicoQtd.size(); i++) {
                                            int p = historicoProduto.get(i);
                                            System.out.printf("Venda #%d: %s | %s | mês %d | " + formatoQtd(porMetro[p])
                                                + " de %s - Valor: R$ %.2f | impostos: R$ %.2f%n",
                                                i + 1, historicoDocumento.get(i), historicoUf.get(i), historicoMes.get(i), historicoQtd.get(i),
                                                especificacoes[p], historicoValores.get(i), historicoImpostos.get(i));
                                        }
                                        System.out.println("Quantidade de vendas: " + historicoQtd.size());
                                        System.out.printf("Faturamento total: R$ %.2f%n", totalVendidoGeral);
                                        System.out.printf("Impostos recolhidos: R$ %.2f%n", totalImpostosGeral);
                                    }
                                    break;

                                case 5: 
                                    System.out.println("--- METRAGEM EM ESTOQUE ---");
                                    double metrosRolos = 0;
                                    double metrosCabos = 0;

                                    System.out.println("Rolos, venda por metro:");
                                    for (int i = 0; i < totalProdutos; i++) {
                                        if (porMetro[i]) {
                                            double inicial = arredondar(estoques[i] + vendidos[i]);
                                            String status;
                                            if (precisaRepor(estoques[i], estoquesMinimos[i])) {
                                                status = "REPOR";
                                            } else {
                                                status = "OK";
                                            }
                                            System.out.printf("- %s | Sobram: %.2f m de %.2f m = %.1f%% | Vendidos: %.2f m | Mínimo: %.2f m | %s%n",
                                                especificacoes[i], estoques[i], inicial, percentualRestante(estoques[i], inicial),
                                                vendidos[i], estoquesMinimos[i], status);
                                            metrosRolos += estoques[i];
                                        }
                                    }

                                    System.out.println("Cabos prontos, unidades x comprimento:");
                                    for (int i = 0; i < totalProdutos; i++) {
                                        if (!porMetro[i]) {
                                            if (comprimentos[i] > 0) {
                                                double metros = metragemDeCabos(estoques[i], comprimentos[i]);
                                                System.out.printf("- %s | %.0f un x %.2f m = %.2f m%n",
                                                    especificacoes[i], estoques[i], comprimentos[i], metros);
                                                metrosCabos += metros;
                                            } else {
                                                System.out.printf("- %s | %.0f un | comprimento não informado%n",
                                                    especificacoes[i], estoques[i]);
                                            }
                                        }
                                    }

                                    System.out.printf("%nTotal em rolos: %.2f m%n", metrosRolos);
                                    System.out.printf("Total em cabos prontos: %.2f m%n", metrosCabos);
                                    System.out.printf("METRAGEM TOTAL EM ESTOQUE: %.2f m%n", metrosRolos + metrosCabos);
                                    break;

                                case 6: 
                                    int opcaoFiscal;
                                    do {
                                        System.out.println("--- FISCAL E RELATÓRIOS ---");
                                        System.out.println("Regime atual: " + nomeRegime(regime));
                                        System.out.println("1. Definir regime tributário");
                                        System.out.println("2. Vendas por mês");
                                        System.out.println("3. Vendas por categoria");
                                        System.out.println("4. Curva ABC");
                                        System.out.println("5. Vendas por operador e forma de pagamento");
                                        System.out.println("0. Voltar");
                                        opcaoFiscal = lerInteiro(entrada, "Escolha uma opção: ");

                                        switch (opcaoFiscal) {
                                            case 1: 
                                                System.out.println("\n1 = Simples Nacional | 2 = Lucro Presumido | 3 = Lucro Real");
                                                int novoRegime = 0;
                                                while (novoRegime < 1 || novoRegime > 3) {
                                                    novoRegime = lerInteiro(entrada, "Escolha o regime: ");
                                                    if (novoRegime < 1 || novoRegime > 3) {
                                                        System.out.println("[ERRO] Digite 1, 2 ou 3.");
                                                    }
                                                }
                                                regime = novoRegime;
                                                System.out.println("Regime definido: " + nomeRegime(regime)
                                                    + " | Venda interna: " + descricaoTributos(regime, "SP")
                                                    + " | Interestadual: " + descricaoTributos(regime, "RJ"));
                                                break;

                                            case 2: 
                                                int mesFiltro = 0;
                                                while (!mesValido(mesFiltro)) {
                                                    mesFiltro = lerInteiro(entrada, "Digite o mês, de 1 a 12: ");
                                                    if (!mesValido(mesFiltro)) {
                                                        System.out.println("[ERRO] O mês deve estar entre 1 e 12.");
                                                    }
                                                }
                                                System.out.println("\n--- VENDAS DO MÊS " + mesFiltro + " ---");
                                                double totalMes = 0;
                                                double impostosMes = 0;
                                                int vendasMes = 0;
                                                for (int i = 0; i < historicoQtd.size(); i++) {
                                                    if (historicoMes.get(i) == mesFiltro) {
                                                        int p = historicoProduto.get(i);
                                                        System.out.printf("- %s | %s | " + formatoQtd(porMetro[p]) + " de %s - R$ %.2f%n",
                                                            historicoDocumento.get(i), historicoUf.get(i), historicoQtd.get(i), especificacoes[p], historicoValores.get(i));
                                                        totalMes = arredondar(totalMes + historicoValores.get(i));
                                                        impostosMes = arredondar(impostosMes + historicoImpostos.get(i));
                                                        vendasMes++;
                                                    }
                                                }
                                                System.out.printf("Vendas no mês %d: %d | Faturamento: R$ %.2f | Impostos: R$ %.2f%n",
                                                    mesFiltro, vendasMes, totalMes, impostosMes);
                                                break;

                                            case 3: 
                                                System.out.println("\n--- VENDAS POR CATEGORIA ---");
                                                if (historicoQtd.size() == 0) {
                                                    System.out.println("Nenhuma venda realizada até o momento.");
                                                } else {
                                                    double maiorCategoria = -1;
                                                    String nomeMaior = "";
                                                    for (int c = 0; c < totalProdutos; c++) {

                                                        boolean jaMostrada = false;
                                                        for (int j = 0; j < c; j++) {
                                                            if (categorias[j].equals(categorias[c])) {
                                                                jaMostrada = true;
                                                            }
                                                        }
                                                        if (!jaMostrada) {
                                                            double totalCategoria = 0;
                                                            int vendasCategoria = 0;
                                                            for (int i = 0; i < historicoQtd.size(); i++) {
                                                                if (categorias[historicoProduto.get(i)].equals(categorias[c])) {
                                                                    totalCategoria = arredondar(totalCategoria + historicoValores.get(i));
                                                                    vendasCategoria++;
                                                                }
                                                            }
                                                            System.out.printf("- %s: vendas %d | R$ %.2f | %.1f%%%n", categorias[c],
                                                                vendasCategoria, totalCategoria, participacao(totalCategoria, totalVendidoGeral));
                                                            if (totalCategoria > maiorCategoria) {
                                                                maiorCategoria = totalCategoria;
                                                                nomeMaior = categorias[c];
                                                            }
                                                        }
                                                    }
                                                    System.out.println("Categoria com maior faturamento: " + nomeMaior);

                                                    double totalHdmi = 0;
                                                    double totalUsb = 0;
                                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                                        String cat = categorias[historicoProduto.get(i)];
                                                        if (cat.equals("HDMI")) {
                                                            totalHdmi = arredondar(totalHdmi + historicoValores.get(i));
                                                        } else if (cat.equals("USB")) {
                                                            totalUsb = arredondar(totalUsb + historicoValores.get(i));
                                                        }
                                                    }
                                                    if (totalHdmi > totalUsb) {
                                                        System.out.printf("Comparativo: HDMI vendeu R$ %.2f, mais que USB, com R$ %.2f.%n", totalHdmi, totalUsb);
                                                    } else if (totalUsb > totalHdmi) {
                                                        System.out.printf("Comparativo: USB vendeu R$ %.2f, mais que HDMI, com R$ %.2f.%n", totalUsb, totalHdmi);
                                                    } else {
                                                        System.out.printf("Comparativo: HDMI e USB empatados em R$ %.2f.%n", totalHdmi);
                                                    }
                                                }
                                                break;

                                            case 4:
                                                System.out.println("\n--- CURVA ABC POR FATURAMENTO ---");
                                                if (historicoQtd.size() == 0) {
                                                    System.out.println("Nenhuma venda realizada até o momento.");
                                                } else {
                                       
                                                    double[] faturadoProduto = new double[totalProdutos];
                                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                                        int p = historicoProduto.get(i);
                                                        faturadoProduto[p] = arredondar(faturadoProduto[p] + historicoValores.get(i));
                                                    }
                                 
                                                    int[] ordem = new int[totalProdutos];
                                                    for (int i = 0; i < totalProdutos; i++) {
                                                        ordem[i] = i;
                                                    }
                                                    for (int i = 0; i < totalProdutos - 1; i++) {
                                                        for (int j = 0; j < totalProdutos - 1 - i; j++) {
                                                            if (faturadoProduto[ordem[j]] < faturadoProduto[ordem[j + 1]]) {
                                                                int troca = ordem[j];
                                                                ordem[j] = ordem[j + 1];
                                                                ordem[j + 1] = troca;
                                                            }
                                                        }
                                                    }
                                            
                                                    double acumulado = 0;
                                                    int qtdA = 0;
                                                    int qtdB = 0;
                                                    int qtdC = 0;
                                                    for (int i = 0; i < totalProdutos; i++) {
                                                        int p = ordem[i];
                                                        String classe = classeABC(acumulado);
                                                        double parte = participacao(faturadoProduto[p], totalVendidoGeral);
                                                        acumulado += parte;
                                                        if (classe.equals("A")) {
                                                            qtdA++;
                                                        } else if (classe.equals("B")) {
                                                            qtdB++;
                                                        } else {
                                                            qtdC++;
                                                        }
                                                        if (faturadoProduto[p] > 0) {
                                                            System.out.printf("%s | %s | R$ %.2f | %.1f%% | acumulado %.1f%% | Classe %s | giro %.1f%%%n",
                                                                classe, skus[p], faturadoProduto[p], parte, acumulado, classe,
                                                                percentualGiro(vendidos[p], estoques[p]));
                                                        }
                                                    }
                                                    System.out.println("Produtos sem venda entram na classe C.");
                                                    System.out.println("Giro = parte do estoque que já foi vendida: vendido dividido por vendido + estoque atual.");
                                                    System.out.printf("Resumo: A = %d | B = %d | C = %d produtos%n", qtdA, qtdB, qtdC);
                                                }
                                                break;

                                            case 5: 
                                                System.out.println("--- VENDAS POR OPERADOR ---");
                                                if (historicoQtd.size() == 0) {
                                                    System.out.println("Nenhuma venda realizada até o momento.");
                                                } else {
                                                    for (int u = 0; u < usuarioLogin.size(); u++) {
                                                        int vendasOperador = 0;
                                                        double totalOperador = 0;
                                                        for (int i = 0; i < historicoQtd.size(); i++) {
                                                            if (historicoVendedor.get(i).equals(usuarioLogin.get(u))) {
                                                                vendasOperador++;
                                                                totalOperador = arredondar(totalOperador + historicoValores.get(i));
                                                            }
                                                        }
                                                        if (vendasOperador > 0 || usuarioPerfil.get(u).equals("VENDEDOR")) {
                                                            System.out.printf("- %s - %s: vendas %d | R$ %.2f | %.1f%%%n", usuarioLogin.get(u),
                                                                usuarioPerfil.get(u), vendasOperador, totalOperador,
                                                                participacao(totalOperador, totalVendidoGeral));
                                                        }
                                                    }
                                                    System.out.println("--- POR FORMA DE PAGAMENTO ---");
                                                    String[] formas = {"No balcão/vendedor", "Pix", "Cartão de débito", "Cartão de crédito", "Boleto"};
                                                    for (int f = 0; f < formas.length; f++) {
                                                        int vendasForma = 0;
                                                        double totalForma = 0;
                                                        for (int i = 0; i < historicoQtd.size(); i++) {
                                                            if (historicoPagamento.get(i).equals(formas[f])) {
                                                                vendasForma++;
                                                                totalForma = arredondar(totalForma + historicoValores.get(i));
                                                            }
                                                        }
                                                        if (vendasForma > 0) {
                                                            System.out.printf("- %s: vendas %d | R$ %.2f%n", formas[f], vendasForma, totalForma);
                                                        }
                                                    }
                                                }
                                                break;

                                            case 0:
                                                break;

                                            default:
                                                System.out.println("Opção inválida! Escolha entre 0 e 5.");
                                        }
                                    } while (opcaoFiscal != 0);
                                    break;

                                case 7: 
                                    System.out.println("--- REPOR ESTOQUE ---");
                                    for (int i = 0; i < totalProdutos; i++) {
                                        System.out.printf("[%d] %s - %s | Estoque: " + formatoQtd(porMetro[i]) + " | Mínimo: "
                                            + formatoQtd(porMetro[i]) + " | %s%n", i, skus[i], especificacoes[i], estoques[i],
                                            estoquesMinimos[i], situacaoEstoque(estoques[i], estoquesMinimos[i]));
                                    }
                                    int indiceRepor = lerInteiro(entrada, "Digite o índice do produto que vai repor: ");
                                    if (indiceRepor < 0 || indiceRepor >= totalProdutos) {
                                        System.out.println("[ERRO] Produto inválido.");
                                    } else {
                                        double qtdRepor;
                                        if (porMetro[indiceRepor]) {
                                            qtdRepor = lerDuasPartes(entrada, "Metros que chegaram: ", "Centímetros, de 0 a 99: ");
                                        } else {
                                            qtdRepor = lerInteiro(entrada, "Unidades que chegaram: ");
                                        }
                                        try {
                                            estoques[indiceRepor] = estoqueAposReposicao(estoques[indiceRepor], qtdRepor, quantidadeMaxima);
                                            System.out.printf("Estoque reposto! %s agora tem " + formatoQtd(porMetro[indiceRepor])
                                                + " - %s.%n", especificacoes[indiceRepor], estoques[indiceRepor],
                                                situacaoEstoque(estoques[indiceRepor], estoquesMinimos[indiceRepor]));
                                        } catch (IllegalArgumentException e) {
                                            System.out.println("[ERRO] " + e.getMessage()); 
                                        }
                                    }
                                    break;

                                case 8: 
                                    int opcaoArquivo;
                                    do {
                                        System.out.println("--- ARQUIVOS ---");
                                        System.out.println("1. Salvar estoque e vendas em " + arquivoDados);
                                        System.out.println("2. Carregar estoque e vendas salvos");
                                        System.out.println("3. Exportar relatório para a contabilidade em " + arquivoRelatorio);
                                        System.out.println("0. Voltar");
                                        opcaoArquivo = lerInteiro(entrada, "Escolha uma opção: ");

                                        switch (opcaoArquivo) {
                                            case 1: 
                                                try (BufferedWriter arquivo = new BufferedWriter(new FileWriter(arquivoDados))) {
                                                    gravarLinha(arquivo, "ESTOQUE-TRIBUTAVEL v3");
                                                    gravarLinha(arquivo, "" + totalProdutos);
                                                    for (int i = 0; i < totalProdutos; i++) {
                                                        gravarLinha(arquivo, skus[i]);
                                                        gravarLinha(arquivo, categorias[i]);
                                                        gravarLinha(arquivo, especificacoes[i]);
                                                        gravarValor(arquivo, precos[i]);
                                                        gravarValor(arquivo, estoques[i]);
                                                        gravarValor(arquivo, estoquesMinimos[i]);
                                                        if (porMetro[i]) {
                                                            gravarLinha(arquivo, "1");
                                                        } else {
                                                            gravarLinha(arquivo, "0");
                                                        }
                                                        gravarValor(arquivo, comprimentos[i]);
                                                        gravarValor(arquivo, vendidos[i]);
                                                        gravarLinha(arquivo, "" + ncms[i]);
                                                        gravarLinha(arquivo, "" + cests[i]);
                                                    }
                                                    gravarLinha(arquivo, "" + regime);
                                                    gravarLinha(arquivo, "" + numeroNfce);
                                                    gravarLinha(arquivo, "" + numeroNfe);
                                                    gravarLinha(arquivo, "" + historicoQtd.size());
                                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                                        gravarValor(arquivo, historicoQtd.get(i));
                                                        gravarValor(arquivo, historicoValores.get(i));
                                                        gravarValor(arquivo, historicoImpostos.get(i));
                                                        gravarLinha(arquivo, "" + historicoProduto.get(i));
                                                        gravarLinha(arquivo, "" + historicoMes.get(i));
                                                        gravarLinha(arquivo, historicoUf.get(i));
                                                        gravarLinha(arquivo, historicoDocumento.get(i));
                                                        gravarLinha(arquivo, historicoVendedor.get(i));
                                                        gravarLinha(arquivo, "" + historicoCliente.get(i));
                                                        gravarLinha(arquivo, historicoPagamento.get(i));
                                                    }
                                                    gravarLinha(arquivo, "" + usuarioLogin.size());
                                                    for (int u = 0; u < usuarioLogin.size(); u++) {
                                                        gravarLinha(arquivo, usuarioLogin.get(u));
                                                        gravarLinha(arquivo, usuarioApelido.get(u));
                                                        gravarLinha(arquivo, usuarioSenha.get(u));
                                                        gravarLinha(arquivo, usuarioPerfil.get(u));
                                                        if (usuarioAtivo.get(u)) {
                                                            gravarLinha(arquivo, "1");
                                                        } else {
                                                            gravarLinha(arquivo, "0");
                                                        }
                                                        gravarLinha(arquivo, "" + usuarioCliente.get(u));
                                                    }
                                                    gravarLinha(arquivo, "" + clienteNome.size());
                                                    for (int c = 0; c < clienteNome.size(); c++) {
                                                        gravarLinha(arquivo, clienteNome.get(c));
                                                        gravarLinha(arquivo, clienteDocumento.get(c));
                                                    }
                                                    gravarLinha(arquivo, "" + orcamentoCliente.size());
                                                    for (int o = 0; o < orcamentoCliente.size(); o++) {
                                                        gravarLinha(arquivo, "" + orcamentoCliente.get(o));
                                                        gravarLinha(arquivo, orcamentoVendedor.get(o));
                                                        gravarLinha(arquivo, orcamentoUf.get(o));
                                                        gravarLinha(arquivo, orcamentoStatus.get(o));
                                                    }
                                                    gravarLinha(arquivo, "" + itemOrcamento.size());
                                                    for (int i = 0; i < itemOrcamento.size(); i++) {
                                                        gravarLinha(arquivo, "" + itemOrcamento.get(i));
                                                        gravarLinha(arquivo, "" + itemProduto.get(i));
                                                        gravarValor(arquivo, itemQtd.get(i));
                                                    }
                                                    System.out.println("Dados salvos em " + arquivoDados + ": produtos " + totalProdutos
                                                        + " | vendas " + historicoQtd.size() + " | usuários " + usuarioLogin.size()
                                                        + " | clientes " + clienteNome.size() + " | orçamentos " + orcamentoCliente.size());
                                                } catch (IOException e) {
                                                    System.out.println("[ERRO] Não foi possível salvar o arquivo " + arquivoDados + ". Confira se a pasta permite gravar.");
                                                }
                                                break;

                                            case 2:
                                                try (Scanner leitor = new Scanner(new File(arquivoDados))) {
                                                    if (!leitor.nextLine().equals("ESTOQUE-TRIBUTAVEL v3")) {
                                                        throw new IllegalArgumentException("o arquivo não foi gravado por este sistema.");
                                                    }
                                                    int lidoProdutos = lerInteiroArquivo(leitor);
                                                    if (lidoProdutos < 0 || lidoProdutos > capacidade) {
                                                        throw new IllegalArgumentException("quantidade de produtos fora do limite: " + lidoProdutos + ".");
                                                    }
                                                    String[] lidoSkus = new String[capacidade];
                                                    String[] lidoCategorias = new String[capacidade];
                                                    String[] lidoEspecificacoes = new String[capacidade];
                                                    double[] lidoPrecos = new double[capacidade];
                                                    double[] lidoEstoques = new double[capacidade];
                                                    double[] lidoMinimos = new double[capacidade];
                                                    boolean[] lidoPorMetro = new boolean[capacidade];
                                                    double[] lidoComprimentos = new double[capacidade];
                                                    double[] lidoVendidos = new double[capacidade];
                                                    int[] lidoNcms = new int[capacidade];
                                                    int[] lidoCests = new int[capacidade];
                                                    for (int i = 0; i < lidoProdutos; i++) {
                                                        lidoSkus[i] = leitor.nextLine();
                                                        lidoCategorias[i] = leitor.nextLine();
                                                        lidoEspecificacoes[i] = leitor.nextLine();
                                                        lidoPrecos[i] = lerValorArquivo(leitor);
                                                        lidoEstoques[i] = lerValorArquivo(leitor);
                                                        lidoMinimos[i] = lerValorArquivo(leitor);
                                                        lidoPorMetro[i] = lerInteiroArquivo(leitor) == 1;
                                                        lidoComprimentos[i] = lerValorArquivo(leitor);
                                                        lidoVendidos[i] = lerValorArquivo(leitor);
                                                        lidoNcms[i] = lerInteiroArquivo(leitor);
                                                        lidoCests[i] = lerInteiroArquivo(leitor);
                                                        if (!precoValido(lidoPrecos[i], precoMaximo) || lidoEstoques[i] < 0
                                                            || lidoEstoques[i] > quantidadeMaxima) {
                                                            throw new IllegalArgumentException("preço ou estoque inválido no produto " + lidoSkus[i] + ".");
                                                        }
                                                    }
                                                    int lidoRegime = lerInteiroArquivo(leitor);
                                                    int lidoNfce = lerInteiroArquivo(leitor);
                                                    int lidoNfe = lerInteiroArquivo(leitor);
                                                    int lidoVendas = lerInteiroArquivo(leitor);
                                                    if (lidoRegime < 1 || lidoRegime > 3 || lidoVendas < 0) {
                                                        throw new IllegalArgumentException("regime ou quantidade de vendas inválidos.");
                                                    }
                                                    ArrayList<Double> lidoQtd = new ArrayList<>();
                                                    ArrayList<Double> lidoValores = new ArrayList<>();
                                                    ArrayList<Double> lidoImpostos = new ArrayList<>();
                                                    ArrayList<Integer> lidoProduto = new ArrayList<>();
                                                    ArrayList<Integer> lidoMes = new ArrayList<>();
                                                    ArrayList<String> lidoUf = new ArrayList<>();
                                                    ArrayList<String> lidoDocumento = new ArrayList<>();
                                                    ArrayList<String> lidoVendedor = new ArrayList<>();
                                                    ArrayList<Integer> lidoClienteVenda = new ArrayList<>();
                                                    ArrayList<String> lidoPagamento = new ArrayList<>();
                                                    for (int i = 0; i < lidoVendas; i++) {
                                                        lidoQtd.add(lerValorArquivo(leitor));
                                                        lidoValores.add(lerValorArquivo(leitor));
                                                        lidoImpostos.add(lerValorArquivo(leitor));
                                                        int produtoLido = lerInteiroArquivo(leitor);
                                                        int mesLido = lerInteiroArquivo(leitor);
                                                        if (produtoLido < 0 || produtoLido >= lidoProdutos || !mesValido(mesLido)) {
                                                            throw new IllegalArgumentException("produto ou mês inválido na venda " + (i + 1) + ".");
                                                        }
                                                        lidoProduto.add(produtoLido);
                                                        lidoMes.add(mesLido);
                                                        lidoUf.add(leitor.nextLine());
                                                        lidoDocumento.add(leitor.nextLine());
                                                        lidoVendedor.add(leitor.nextLine());
                                                        lidoClienteVenda.add(lerInteiroArquivo(leitor));
                                                        lidoPagamento.add(leitor.nextLine());
                                                    }

                                                    int lidoUsuarios = lerInteiroArquivo(leitor);
                                                    ArrayList<String> lidoLogin = new ArrayList<>();
                                                    ArrayList<String> lidoApelido = new ArrayList<>();
                                                    ArrayList<String> lidoSenha = new ArrayList<>();
                                                    ArrayList<String> lidoPerfil = new ArrayList<>();
                                                    ArrayList<Boolean> lidoAtivo = new ArrayList<>();
                                                    ArrayList<Integer> lidoUsuarioCliente = new ArrayList<>();
                                                    boolean temAdministrador = false;
                                                    for (int u = 0; u < lidoUsuarios; u++) {
                                                        lidoLogin.add(leitor.nextLine());
                                                        lidoApelido.add(leitor.nextLine());
                                                        lidoSenha.add(leitor.nextLine());
                                                        String perfilLido = leitor.nextLine();
                                                        boolean ativoLido = lerInteiroArquivo(leitor) == 1;
                                                        if (!perfilLido.equals("ADMINISTRADOR") && !perfilLido.equals("VENDEDOR")
                                                            && !perfilLido.equals("CLIENTE")) {
                                                            throw new IllegalArgumentException("perfil desconhecido no usuário " + lidoLogin.get(u) + ".");
                                                        }
                                                        if (perfilLido.equals("ADMINISTRADOR") && ativoLido) {
                                                            temAdministrador = true;
                                                        }
                                                        lidoPerfil.add(perfilLido);
                                                        lidoAtivo.add(ativoLido);
                                                        lidoUsuarioCliente.add(lerInteiroArquivo(leitor));
                                                    }
                                                    if (!temAdministrador) {
                                                        throw new IllegalArgumentException("o arquivo não tem nenhum administrador ativo.");
                                                    }

                                                    int lidoClientes = lerInteiroArquivo(leitor);
                                                    ArrayList<String> lidoClienteNome = new ArrayList<>();
                                                    ArrayList<String> lidoClienteDocumento = new ArrayList<>();
                                                    for (int c = 0; c < lidoClientes; c++) {
                                                        lidoClienteNome.add(leitor.nextLine());
                                                        lidoClienteDocumento.add(leitor.nextLine());
                                                    }
                                                    for (int u = 0; u < lidoUsuarios; u++) {
                                                        if (lidoUsuarioCliente.get(u) < -1 || lidoUsuarioCliente.get(u) >= lidoClientes
                                                            || (lidoPerfil.get(u).equals("CLIENTE") && lidoUsuarioCliente.get(u) == -1)) {
                                                            throw new IllegalArgumentException("cliente inválido no usuário " + lidoLogin.get(u) + ".");
                                                        }
                                                    }
                                                    for (int i = 0; i < lidoVendas; i++) {
                                                        if (lidoClienteVenda.get(i) < -1 || lidoClienteVenda.get(i) >= lidoClientes) {
                                                            throw new IllegalArgumentException("cliente inválido na venda " + (i + 1) + ".");
                                                        }
                                                    }

                                                    int lidoOrcamentos = lerInteiroArquivo(leitor);
                                                    ArrayList<Integer> lidoOrcCliente = new ArrayList<>();
                                                    ArrayList<String> lidoOrcVendedor = new ArrayList<>();
                                                    ArrayList<String> lidoOrcUf = new ArrayList<>();
                                                    ArrayList<String> lidoOrcStatus = new ArrayList<>();
                                                    for (int o = 0; o < lidoOrcamentos; o++) {
                                                        int clienteOrc = lerInteiroArquivo(leitor);
                                                        if (clienteOrc < 0 || clienteOrc >= lidoClientes) {
                                                            throw new IllegalArgumentException("cliente inválido no orçamento " + (o + 1) + ".");
                                                        }
                                                        lidoOrcCliente.add(clienteOrc);
                                                        lidoOrcVendedor.add(leitor.nextLine());
                                                        lidoOrcUf.add(leitor.nextLine());
                                                        lidoOrcStatus.add(leitor.nextLine());
                                                    }
                                                    int lidoItens = lerInteiroArquivo(leitor);
                                                    ArrayList<Integer> lidoItemOrc = new ArrayList<>();
                                                    ArrayList<Integer> lidoItemProduto = new ArrayList<>();
                                                    ArrayList<Double> lidoItemQtd = new ArrayList<>();
                                                    for (int i = 0; i < lidoItens; i++) {
                                                        int orcLido = lerInteiroArquivo(leitor);
                                                        int prodLido = lerInteiroArquivo(leitor);
                                                        if (orcLido < 1 || orcLido > lidoOrcamentos || prodLido < 0 || prodLido >= lidoProdutos) {
                                                            throw new IllegalArgumentException("item de orçamento inválido nº " + (i + 1) + ".");
                                                        }
                                                        lidoItemOrc.add(orcLido);
                                                        lidoItemProduto.add(prodLido);
                                                        lidoItemQtd.add(lerValorArquivo(leitor));
                                                    }

                                                    for (int i = 0; i < capacidade; i++) {
                                                        skus[i] = lidoSkus[i];
                                                        categorias[i] = lidoCategorias[i];
                                                        especificacoes[i] = lidoEspecificacoes[i];
                                                        precos[i] = lidoPrecos[i];
                                                        estoques[i] = lidoEstoques[i];
                                                        estoquesMinimos[i] = lidoMinimos[i];
                                                        porMetro[i] = lidoPorMetro[i];
                                                        comprimentos[i] = lidoComprimentos[i];
                                                        vendidos[i] = lidoVendidos[i];
                                                        ncms[i] = lidoNcms[i];
                                                        cests[i] = lidoCests[i];
                                                    }
                                                    totalProdutos = lidoProdutos;
                                                    regime = lidoRegime;
                                                    numeroNfce = lidoNfce;
                                                    numeroNfe = lidoNfe;
                                                    historicoQtd.clear();
                                                    historicoValores.clear();
                                                    historicoImpostos.clear();
                                                    historicoProduto.clear();
                                                    historicoMes.clear();
                                                    historicoUf.clear();
                                                    historicoDocumento.clear();
                                                    historicoVendedor.clear();
                                                    historicoCliente.clear();
                                                    historicoPagamento.clear();
                                                    totalVendidoGeral = 0;
                                                    totalImpostosGeral = 0;
                                                    for (int i = 0; i < lidoVendas; i++) {
                                                        historicoQtd.add(lidoQtd.get(i));
                                                        historicoValores.add(lidoValores.get(i));
                                                        historicoImpostos.add(lidoImpostos.get(i));
                                                        historicoProduto.add(lidoProduto.get(i));
                                                        historicoMes.add(lidoMes.get(i));
                                                        historicoUf.add(lidoUf.get(i));
                                                        historicoDocumento.add(lidoDocumento.get(i));
                                                        historicoVendedor.add(lidoVendedor.get(i));
                                                        historicoCliente.add(lidoClienteVenda.get(i));
                                                        historicoPagamento.add(lidoPagamento.get(i));
                                                        totalVendidoGeral = arredondar(totalVendidoGeral + lidoValores.get(i));
                                                        totalImpostosGeral = arredondar(totalImpostosGeral + lidoImpostos.get(i));
                                                    }
                                                    usuarioLogin.clear();
                                                    usuarioApelido.clear();
                                                    usuarioSenha.clear();
                                                    usuarioPerfil.clear();
                                                    usuarioAtivo.clear();
                                                    usuarioCliente.clear();
                                                    for (int u = 0; u < lidoUsuarios; u++) {
                                                        usuarioLogin.add(lidoLogin.get(u));
                                                        usuarioApelido.add(lidoApelido.get(u));
                                                        usuarioSenha.add(lidoSenha.get(u));
                                                        usuarioPerfil.add(lidoPerfil.get(u));
                                                        usuarioAtivo.add(lidoAtivo.get(u));
                                                        usuarioCliente.add(lidoUsuarioCliente.get(u));
                                                    }
                                                    clienteNome.clear();
                                                    clienteDocumento.clear();
                                                    for (int c = 0; c < lidoClientes; c++) {
                                                        clienteNome.add(lidoClienteNome.get(c));
                                                        clienteDocumento.add(lidoClienteDocumento.get(c));
                                                    }
                                                    orcamentoCliente.clear();
                                                    orcamentoVendedor.clear();
                                                    orcamentoUf.clear();
                                                    orcamentoStatus.clear();
                                                    for (int o = 0; o < lidoOrcamentos; o++) {
                                                        orcamentoCliente.add(lidoOrcCliente.get(o));
                                                        orcamentoVendedor.add(lidoOrcVendedor.get(o));
                                                        orcamentoUf.add(lidoOrcUf.get(o));
                                                        orcamentoStatus.add(lidoOrcStatus.get(o));
                                                    }
                                                    itemOrcamento.clear();
                                                    itemProduto.clear();
                                                    itemQtd.clear();
                                                    for (int i = 0; i < lidoItens; i++) {
                                                        itemOrcamento.add(lidoItemOrc.get(i));
                                                        itemProduto.add(lidoItemProduto.get(i));
                                                        itemQtd.add(lidoItemQtd.get(i));
                                                    }
                                                    System.out.println("Dados carregados de " + arquivoDados + ": produtos " + totalProdutos
                                                        + " | vendas " + historicoQtd.size() + " | usuários " + usuarioLogin.size()
                                                        + " | clientes " + clienteNome.size() + " | orçamentos " + orcamentoCliente.size());
                                                } catch (FileNotFoundException e) {
                                                    System.out.println("[ERRO] Nenhum arquivo salvo foi encontrado: " + arquivoDados + ".");
                                                } catch (IllegalArgumentException e) {
                                                    System.out.println("[ERRO] Arquivo inválido: " + e.getMessage() + " Nada foi alterado.");
                                                } catch (Exception e) {
                                                    System.out.println("[ERRO] O arquivo está incompleto ou corrompido. Nada foi alterado.");
                                                }
                                                break;

                                            case 3: 
                                                try (BufferedWriter relatorio = new BufferedWriter(new FileWriter(arquivoRelatorio))) {
                                                    gravarLinha(relatorio, "RELATÓRIO CONTÁBIL - SISTEMA DE ESTOQUE TRIBUTÁVEL");
                                                    gravarLinha(relatorio, "Regime tributário atual: " + nomeRegime(regime));
                                                    gravarLinha(relatorio, "");
                                                    gravarLinha(relatorio, "VENDAS");
                                                    if (historicoQtd.size() == 0) {
                                                        gravarLinha(relatorio, "Nenhuma venda realizada.");
                                                    }
                                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                                        int p = historicoProduto.get(i);
                                                        String qtdTexto;
                                                        if (porMetro[p]) {
                                                            qtdTexto = formatarDuasCasas(historicoQtd.get(i)) + " m";
                                                        } else {
                                                            qtdTexto = textoInteiro(arredondarInteiro(historicoQtd.get(i))) + " un";
                                                        }
                                                        gravarLinha(relatorio, "Venda #" + (i + 1) + " | " + historicoDocumento.get(i) + " | "
                                                            + historicoUf.get(i) + " | mês " + historicoMes.get(i) + " | " + qtdTexto + " de "
                                                            + especificacoes[p] + " | Valor: R$ " + formatarDuasCasas(historicoValores.get(i))
                                                            + " | Impostos: R$ " + formatarDuasCasas(historicoImpostos.get(i))
                                                            + " | Vendedor: " + historicoVendedor.get(i) + " | Pagamento: " + historicoPagamento.get(i));
                                                    }
                                                    gravarLinha(relatorio, "");
                                                    gravarLinha(relatorio, "RESUMO POR MÊS");
                                                    for (int mes = 1; mes <= 12; mes++) {
                                                        int vendasNoMes = 0;
                                                        double faturadoNoMes = 0;
                                                        double impostosNoMes = 0;
                                                        for (int i = 0; i < historicoQtd.size(); i++) {
                                                            if (historicoMes.get(i) == mes) {
                                                                vendasNoMes++;
                                                                faturadoNoMes = arredondar(faturadoNoMes + historicoValores.get(i));
                                                                impostosNoMes = arredondar(impostosNoMes + historicoImpostos.get(i));
                                                            }
                                                        }
                                                        if (vendasNoMes > 0) {
                                                            gravarLinha(relatorio, "Mês " + mes + ": vendas " + vendasNoMes + " | Faturamento: R$ "
                                                                + formatarDuasCasas(faturadoNoMes) + " | Impostos: R$ " + formatarDuasCasas(impostosNoMes));
                                                        }
                                                    }
                                                    gravarLinha(relatorio, "");
                                                    gravarLinha(relatorio, "Quantidade de vendas: " + historicoQtd.size());
                                                    gravarLinha(relatorio, "Faturamento total: R$ " + formatarDuasCasas(totalVendidoGeral));
                                                    gravarLinha(relatorio, "Impostos recolhidos: R$ " + formatarDuasCasas(totalImpostosGeral));
                                                    System.out.println("Relatório exportado para " + arquivoRelatorio + " - vendas: "
                                                        + historicoQtd.size() + ".");
                                                } catch (IOException e) {
                                                    System.out.println("[ERRO] Não foi possível exportar o relatório " + arquivoRelatorio + ". Confira se a pasta permite gravar.");
                                                }
                                                break;

                                            case 0:
                                                break;

                                            default:
                                                System.out.println("Opção inválida! Escolha entre 0 e 3.");
                                        }
                                    } while (opcaoArquivo != 0);
                                    break;

                                case 9: 
                                    int opcaoUsuario;
                                    do {
                                        System.out.println("--- USUÁRIOS E PERMISSÕES ---");
                                        for (int u = 0; u < usuarioLogin.size(); u++) {
                                            String situacaoUsuario = "ATIVO";
                                            if (!usuarioAtivo.get(u)) {
                                                situacaoUsuario = "BLOQUEADO";
                                            }
                                            System.out.println("[" + u + "] " + usuarioLogin.get(u) + " | " + usuarioPerfil.get(u)
                                                + " | " + situacaoUsuario);
                                        }
                                        System.out.println("1. Cadastrar usuário: administrador ou vendedor");
                                        System.out.println("2. Mudar o perfil de um usuário");
                                        System.out.println("3. Bloquear ou desbloquear um usuário");
                                        System.out.println("0. Voltar");
                                        opcaoUsuario = lerInteiro(entrada, "Escolha uma opção: ");

                                        switch (opcaoUsuario) {
                                            case 1: 
                                                System.out.print("Login do novo usuário, uma palavra só: ");
                                                String novoLogin = entrada.next();
                                                entrada.nextLine();
                                                try {
                                                    validarNovoLogin(usuarioLogin, usuarioApelido, novoLogin);
                                                    String novaSenha = "";
                                                    while (novaSenha.equals("")) {
                                                        System.out.print("Senha: ");
                                                        novaSenha = entrada.nextLine();
                                                        if (novaSenha.equals("")) {
                                                            System.out.println("[ERRO] A senha não pode ficar vazia.");
                                                        }
                                                    }
                                                    int codigoPerfil = 0;
                                                    while (nomePerfil(codigoPerfil).equals("")) {
                                                        codigoPerfil = lerInteiro(entrada, "Perfil - 1 = Administrador | 2 = Vendedor: ");
                                                        if (nomePerfil(codigoPerfil).equals("")) {
                                                            System.out.println("[ERRO] Digite 1 ou 2. Clientes são cadastrados pelo vendedor.");
                                                        }
                                                    }
                                                    usuarioLogin.add(novoLogin);
                                                    usuarioApelido.add(novoLogin);
                                                    usuarioSenha.add(novaSenha);
                                                    usuarioPerfil.add(nomePerfil(codigoPerfil));
                                                    usuarioAtivo.add(true);
                                                    usuarioCliente.add(-1);
                                                    System.out.println("Usuário " + novoLogin + " cadastrado como " + nomePerfil(codigoPerfil) + ".");
                                                } catch (IllegalArgumentException e) {
                                                    System.out.println("[ERRO] " + e.getMessage());
                                                }
                                                break;

                                            case 2: 
                                            case 3:
                                                int indiceEscolhido = lerInteiro(entrada, "Digite o número [ ] do usuário: ");
                                                if (indiceEscolhido < 0 || indiceEscolhido >= usuarioLogin.size()) {
                                                    System.out.println("[ERRO] Usuário inválido.");
                                                } else if (usuarioLogin.get(indiceEscolhido).equals(loginAtual)) {
                                                    System.out.println("[ERRO] Você não pode mudar o seu próprio acesso.");
                                                } else if (opcaoUsuario == 2) {
                                                    if (usuarioPerfil.get(indiceEscolhido).equals("CLIENTE")) {
                                                        System.out.println("[ERRO] O perfil de um cliente não pode ser trocado.");
                                                    } else {
                                                        int codigoPerfil = 0;
                                                        while (nomePerfil(codigoPerfil).equals("")) {
                                                            codigoPerfil = lerInteiro(entrada, "Novo perfil - 1 = Administrador | 2 = Vendedor: ");
                                                            if (nomePerfil(codigoPerfil).equals("")) {
                                                                System.out.println("[ERRO] Digite 1 ou 2.");
                                                            }
                                                        }
                                                        usuarioPerfil.set(indiceEscolhido, nomePerfil(codigoPerfil));
                                                        System.out.println("Perfil de " + usuarioLogin.get(indiceEscolhido) + " agora é "
                                                            + nomePerfil(codigoPerfil) + ".");
                                                    }
                                                } else {
                                                    usuarioAtivo.set(indiceEscolhido, !usuarioAtivo.get(indiceEscolhido));
                                                    if (usuarioAtivo.get(indiceEscolhido)) {
                                                        System.out.println("Usuário " + usuarioLogin.get(indiceEscolhido) + " desbloqueado.");
                                                    } else {
                                                        System.out.println("Usuário " + usuarioLogin.get(indiceEscolhido) + " bloqueado: não consegue mais entrar.");
                                                    }
                                                }
                                                break;

                                            case 0:
                                                break;

                                            default:
                                                System.out.println("Opção inválida! Escolha entre 0 e 3.");
                                        }
                                    } while (opcaoUsuario != 0);
                                    break;

                                case 10:
                                    System.out.println("Deslogando...");
                                    break;

                                default:
                                    System.out.println("Opção inválida! Escolha entre 1 e 10.");
                            }
                        } while (opcao != 10);
                    }

                    else if (perfilAtual.equals("VENDEDOR")) {
                        do {
                            System.out.println("--- MENU VENDEDOR ---");
                            System.out.println("1. Visão Geral: vendas realizadas e total vendido");
                            System.out.println("2. Realizar Nova Venda");
                            System.out.println("3. Verificar Todas as Vendas Realizadas");
                            System.out.println("4. Clientes: cadastrar e listar");
                            System.out.println("5. Orçamentos: criar, listar e cancelar");
                            System.out.println("6. Sair / Deslogar");
                            opcao = lerInteiro(entrada, "Escolha uma opção: ");

                            switch (opcao) {
                                case 1:
                                    System.out.println("--- VISÃO GERAL DE VENDAS ---");
                                    System.out.println("Quantidade total de vendas realizadas: " + historicoQtd.size());
                                    System.out.printf("Valor total vendido no dia: R$ %.2f%n", totalVendidoGeral);
                                    break;

                                case 2: 
                                    System.out.println("--- PRODUTOS DISPONÍVEIS ---");
                                    double aliquotaSp = aliquotaTotal(regime, aliquotaIcms("SP"));
                                    double aliquotaOutros = aliquotaTotal(regime, aliquotaIcms("RJ"));
                                    System.out.println("Preço unitário com impostos, regime " + nomeRegime(regime)
                                        + ": para SP e para outros estados");
                                    for (int i = 0; i < totalProdutos; i++) {
                                        double precoSp = calcularTotal(precos[i], calcularImposto(precos[i], aliquotaSp));
                                        double precoOutros = calcularTotal(precos[i], calcularImposto(precos[i], aliquotaOutros));
                                        String medida;
                                        if (porMetro[i]) {
                                            medida = "m";
                                        } else {
                                            medida = "un";
                                        }
                                        System.out.printf("[%d] %s - %s | SP: R$ %.2f/%s | Outros estados: R$ %.2f/%s | Estoque: "
                                            + formatoQtd(porMetro[i]) + "%n",
                                            i, skus[i], especificacoes[i], precoSp, medida, precoOutros, medida, estoques[i]);
                                    }

                                    int prodIndex = lerInteiro(entrada, "Digite o índice do produto que deseja vender: ");

                                    if (prodIndex < 0 || prodIndex >= totalProdutos) {
                                        System.out.println("[ERRO] Produto inválido.");
                                    } else if (estoques[prodIndex] == 0) {
                                        System.out.println("[ERRO] Produto esgotado.");
                                    } else {
                                        double qtdVenda;
                                        if (porMetro[prodIndex]) {
                                            System.out.println("Metragem desejada - ex.: 7,50 m = 7 metros e 50 centímetros");
                                            qtdVenda = arredondar(lerDuasPartes(entrada, "  Metros: ", "  Centímetros, de 0 a 99: "));
                                        } else {
                                            qtdVenda = lerInteiro(entrada, "Digite a quantidade desejada: ");
                                        }

                                        if (!quantidadeValida(qtdVenda, estoques[prodIndex])) {
                                            System.out.println("[ERRO] Quantidade inválida ou estoque insuficiente.");
                                        } else if (calcularSubtotal(qtdVenda, precos[prodIndex]) < 0.01) {
                                            System.out.println("[ERRO] O valor da venda ficaria em R$ 0,00. Aumente a quantidade.");
                                        } else {
                                            String ufDestino = "";
                                            while (ufDestino.equals("")) {
                                                System.out.print("UF de destino, ex.: SP, RJ, MG: ");
                                                ufDestino = ufMaiuscula(entrada.nextLine());
                                                if (ufDestino.equals("")) {
                                                    System.out.println("[ERRO] UF inválida. Digite a sigla do estado com 2 letras.");
                                                }
                                            }

                                            boolean balcao = false;
                                            if (ufDestino.equals("SP")) {
                                                String resposta = "";
                                                while (!respostaValida(resposta)) {
                                                    System.out.print("Venda de balcão para consumidor final? S/N: ");
                                                    resposta = entrada.nextLine();
                                                    if (!respostaValida(resposta)) {
                                                        System.out.println("[ERRO] Responda S ou N.");
                                                    }
                                                }
                                                balcao = respostaSim(resposta);
                                            }

                                            int mesVenda = 0;
                                            while (!mesValido(mesVenda)) {
                                                mesVenda = lerInteiro(entrada, "Mês da venda, de 1 a 12: ");
                                                if (!mesValido(mesVenda)) {
                                                    System.out.println("[ERRO] O mês deve estar entre 1 e 12.");
                                                }
                                            }

                                            double aliquota = aliquotaTotal(regime, aliquotaIcms(ufDestino));
                                            double subtotal = calcularSubtotal(qtdVenda, precos[prodIndex]);
                                            double impostoVenda = calcularImposto(subtotal, aliquota);
                                            double valorVenda = calcularTotal(subtotal, impostoVenda);

                                            String tipo = tipoDocumento(ufDestino, balcao);
                                            int numero;
                                            if (tipo.equals("NFC-e")) {
                                                numeroNfce++;
                                                numero = numeroNfce;
                                            } else {
                                                numeroNfe++;
                                                numero = numeroNfe;
                                            }
                                            String documento = tipo + " nº " + numero;

                                            estoques[prodIndex] = arredondar(estoques[prodIndex] - qtdVenda);
                                            vendidos[prodIndex] = arredondar(vendidos[prodIndex] + qtdVenda);

                                            historicoQtd.add(qtdVenda);
                                            historicoValores.add(valorVenda);
                                            historicoImpostos.add(impostoVenda);
                                            historicoProduto.add(prodIndex);
                                            historicoMes.add(mesVenda);
                                            historicoUf.add(ufDestino);
                                            historicoDocumento.add(documento);
                                            historicoVendedor.add(loginAtual);
                                            historicoCliente.add(-1);
                                            historicoPagamento.add("No balcão/vendedor");
                                            totalVendidoGeral = arredondar(totalVendidoGeral + valorVenda);
                                            totalImpostosGeral = arredondar(totalImpostosGeral + impostoVenda);

                                            String operacao;
                                            if (ufDestino.equals("SP")) {
                                                operacao = "- interna";
                                            } else {
                                                operacao = "- interestadual";
                                            }
                                            System.out.println("Venda realizada com sucesso!");
                                            System.out.println("========== " + documento + " ==========");
                                            System.out.printf("Produto: " + formatoQtd(porMetro[prodIndex]) + " de %s | ",
                                                qtdVenda, especificacoes[prodIndex]);
                                            imprimirNcmCest(ncms[prodIndex], cests[prodIndex]);
                                            System.out.println();
                                            System.out.println("Destino: " + ufDestino + " " + operacao + " | Mês: " + mesVenda
                                                + " | Regime: " + nomeRegime(regime));
                                            System.out.println("Tributos: " + descricaoTributos(regime, ufDestino));
                                            System.out.printf("Subtotal: R$ %.2f | Impostos de %.2f%%: R$ %.2f | Total: R$ %.2f%n",
                                                subtotal, aliquota * 100, impostoVenda, valorVenda);

                                            if (porMetro[prodIndex]) {
                                                System.out.printf("Metragem que sobra no rolo: %.2f m%n", estoques[prodIndex]);
                                            }
                                            if (precisaRepor(estoques[prodIndex], estoquesMinimos[prodIndex])) {
                                                System.out.printf("ALERTA: estoque baixo para %s, restam " + formatoQtd(porMetro[prodIndex])
                                                    + ". Necessário reposição.%n", especificacoes[prodIndex], estoques[prodIndex]);
                                            }
                                        }
                                    }
                                    break;
                                case 3:
                                    System.out.println("--- HISTÓRICO DE VENDAS REALIZADAS ---");
                                    if (historicoQtd.size() == 0) {
                                        System.out.println("Nenhuma venda realizada até o momento.");
                                    } else {
                                        for (int i = 0; i < historicoQtd.size(); i++) {
                                            int p = historicoProduto.get(i);
                                            System.out.printf("Venda #%d: %s | " + formatoQtd(porMetro[p]) + " de %s - Valor: R$ %.2f%n",
                                                i + 1, historicoDocumento.get(i), historicoQtd.get(i), especificacoes[p], historicoValores.get(i));
                                        }
                                        System.out.printf("Total Acumulado Vendido: R$ %.2f%n", totalVendidoGeral);
                                    }
                                    break;

                                case 4: 
                                    int opcaoCliente;
                                    do {
                                        System.out.println("--- CLIENTES ---");
                                        System.out.println("1. Cadastrar cliente");
                                        System.out.println("2. Listar clientes");
                                        System.out.println("0. Voltar");
                                        opcaoCliente = lerInteiro(entrada, "Escolha uma opção: ");

                                        switch (opcaoCliente) {
                                            case 1: 
                                                String nomeNovo = "";
                                                while (nomeNovo.equals("")) {
                                                    System.out.print("Nome do cliente: ");
                                                    nomeNovo = entrada.nextLine();
                                                    if (nomeNovo.equals("")) {
                                                        System.out.println("[ERRO] O nome não pode ficar vazio.");
                                                    }
                                                }
                                                System.out.print("CPF ou CNPJ, só números e sem espaços: ");
                                                String documentoNovo = entrada.next();
                                                entrada.nextLine();
                                                try {
                                                    validarNovoLogin(usuarioLogin, usuarioApelido, documentoNovo);
                                                    String senhaCliente = "";
                                                    while (senhaCliente.equals("")) {
                                                        System.out.print("Senha de acesso do cliente: ");
                                                        senhaCliente = entrada.nextLine();
                                                        if (senhaCliente.equals("")) {
                                                            System.out.println("[ERRO] A senha não pode ficar vazia.");
                                                        }
                                                    }
                                                    clienteNome.add(nomeNovo);
                                                    clienteDocumento.add(documentoNovo);
                                                    usuarioLogin.add(documentoNovo);
                                                    usuarioApelido.add(documentoNovo);
                                                    usuarioSenha.add(senhaCliente);
                                                    usuarioPerfil.add("CLIENTE");
                                                    usuarioAtivo.add(true);
                                                    usuarioCliente.add(clienteNome.size() - 1);
                                                    System.out.println("Cliente cadastrado! Código " + (clienteNome.size() - 1)
                                                        + ". Ele entra no sistema com o CPF/CNPJ " + documentoNovo + " e a senha escolhida.");
                                                } catch (IllegalArgumentException e) {
                                                    System.out.println("[ERRO] " + e.getMessage());
                                                }
                                                break;
                                            case 2: 
                                                System.out.println("--- CLIENTES CADASTRADOS ---");
                                                for (int c = 0; c < clienteNome.size(); c++) {
                                                    double comprasCliente = 0;
                                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                                        if (historicoCliente.get(i) == c) {
                                                            comprasCliente = arredondar(comprasCliente + historicoValores.get(i));
                                                        }
                                                    }
                                                    System.out.printf("[%d] %s | CPF/CNPJ: %s | compras: R$ %.2f%n",
                                                        c, clienteNome.get(c), clienteDocumento.get(c), comprasCliente);
                                                }
                                                break;

                                            case 0:
                                                break;

                                            default:
                                                System.out.println("Opção inválida! Escolha entre 0 e 2.");
                                        }
                                    } while (opcaoCliente != 0);
                                    break;

                                case 5: 
                                    int opcaoOrcamento;
                                    do {
                                        System.out.println("--- ORÇAMENTOS ---");
                                        System.out.println("1. Criar orçamento para um cliente");
                                        System.out.println("2. Listar orçamentos");
                                        System.out.println("3. Cancelar orçamento");
                                        System.out.println("0. Voltar");
                                        opcaoOrcamento = lerInteiro(entrada, "Escolha uma opção: ");

                                        switch (opcaoOrcamento) {
                                            case 1: 
                                                for (int c = 0; c < clienteNome.size(); c++) {
                                                    System.out.println("[" + c + "] " + clienteNome.get(c) + " | " + clienteDocumento.get(c));
                                                }
                                                int clienteEscolhido = lerInteiro(entrada, "Código do cliente: ");
                                                if (clienteEscolhido < 0 || clienteEscolhido >= clienteNome.size()) {
                                                    System.out.println("[ERRO] Cliente inválido. Cadastre o cliente antes, no menu 4.");
                                                } else {
                                                    String ufOrcamento = "";
                                                    while (ufOrcamento.equals("")) {
                                                        System.out.print("UF de destino, ex.: SP, RJ, MG: ");
                                                        ufOrcamento = ufMaiuscula(entrada.nextLine());
                                                        if (ufOrcamento.equals("")) {
                                                            System.out.println("[ERRO] UF inválida. Digite a sigla do estado com 2 letras.");
                                                        }
                                                    }
                                                    int numeroOrcamento = orcamentoCliente.size() + 1;
                                                    int itensAdicionados = 0;
                                                    int produtoItem = 0;
                                                    while (produtoItem != -1) {
                                                        System.out.println("Produtos:");
                                                        for (int i = 0; i < totalProdutos; i++) {
                                                            System.out.printf("[%d] %s | R$ %.2f | Estoque: " + formatoQtd(porMetro[i]) + "%n",
                                                                i, especificacoes[i], precos[i], estoques[i]);
                                                        }
                                                        produtoItem = lerInteiro(entrada, "Produto do item, ou -1 para terminar o orçamento: ");
                                                        if (produtoItem == -1) {
                                                            System.out.println("Fim dos itens.");
                                                        } else if (produtoItem < 0 || produtoItem >= totalProdutos) {
                                                            System.out.println("[ERRO] Produto inválido.");
                                                        } else {
                                                            double qtdItem;
                                                            if (porMetro[produtoItem]) {
                                                                qtdItem = arredondar(lerDuasPartes(entrada, "  Metros: ", "  Centímetros, de 0 a 99: "));
                                                            } else {
                                                                qtdItem = lerInteiro(entrada, "Quantidade: ");
                                                            }
                                                            if (!quantidadeValida(qtdItem, estoques[produtoItem])) {
                                                                System.out.println("[ERRO] Quantidade inválida ou maior que o estoque.");
                                                            } else if (calcularSubtotal(qtdItem, precos[produtoItem]) < 0.01) {
                                                                System.out.println("[ERRO] O item ficaria em R$ 0,00. Aumente a quantidade.");
                                                            } else {
                                                                itemOrcamento.add(numeroOrcamento);
                                                                itemProduto.add(produtoItem);
                                                                itemQtd.add(qtdItem);
                                                                itensAdicionados++;
                                                                System.out.println("Item adicionado ao orçamento nº " + numeroOrcamento + ".");
                                                            }
                                                        }
                                                    }
                                                    if (itensAdicionados == 0) {
                                                        System.out.println("[ERRO] Orçamento sem itens não foi criado.");
                                                    } else {
                                                        orcamentoCliente.add(clienteEscolhido);
                                                        orcamentoVendedor.add(loginAtual);
                                                        orcamentoUf.add(ufOrcamento);
                                                        orcamentoStatus.add("ABERTO");
                                                        System.out.println("Orçamento nº " + numeroOrcamento + " criado para "
                                                            + clienteNome.get(clienteEscolhido) + ". O cliente já pode ver e pagar no perfil dele:");
                                                        mostrarOrcamento(numeroOrcamento, itemOrcamento, itemProduto, itemQtd,
                                                            especificacoes, precos, porMetro, regime, ufOrcamento);
                                                    }
                                                }
                                                break;

                                            case 2:
                                                if (orcamentoCliente.size() == 0) {
                                                    System.out.println("Nenhum orçamento criado.");
                                                }
                                                for (int o = 0; o < orcamentoCliente.size(); o++) {
                                                    System.out.println("Orçamento nº " + (o + 1) + " | " + clienteNome.get(orcamentoCliente.get(o))
                                                        + " | vendedor " + orcamentoVendedor.get(o) + " | " + orcamentoStatus.get(o));
                                                    mostrarOrcamento(o + 1, itemOrcamento, itemProduto, itemQtd,
                                                        especificacoes, precos, porMetro, regime, orcamentoUf.get(o));
                                                }
                                                break;

                                            case 3: 
                                                int numeroCancelar = lerInteiro(entrada, "Número do orçamento a cancelar: ");
                                                if (numeroCancelar < 1 || numeroCancelar > orcamentoCliente.size()) {
                                                    System.out.println("[ERRO] Orçamento inexistente.");
                                                } else if (!orcamentoStatus.get(numeroCancelar - 1).equals("ABERTO")) {
                                                    System.out.println("[ERRO] Só dá para cancelar um orçamento ABERTO. Este está "
                                                        + orcamentoStatus.get(numeroCancelar - 1) + ".");
                                                } else {
                                                    orcamentoStatus.set(numeroCancelar - 1, "CANCELADO");
                                                    System.out.println("Orçamento nº " + numeroCancelar + " cancelado.");
                                                }
                                                break;

                                            case 0:
                                                break;

                                            default:
                                                System.out.println("Opção inválida! Escolha entre 0 e 3.");
                                        }
                                    } while (opcaoOrcamento != 0);
                                    break;

                                case 6:
                                    System.out.println("Deslogando...");
                                    break;

                                default:
                                    System.out.println("Opção inválida! Escolha entre 1 e 6.");
                            }
                        } while (opcao != 6);
                    }
                    else if (perfilAtual.equals("CLIENTE")) {
                        do {
                            System.out.println("--- MENU CLIENTE ---");
                            System.out.println("1. Meus orçamentos: itens adicionados pelo vendedor");
                            System.out.println("2. Pagar um orçamento");
                            System.out.println("3. Minhas compras");
                            System.out.println("4. Sair / Deslogar");
                            opcao = lerInteiro(entrada, "Escolha uma opção: ");

                            switch (opcao) {
                                case 1: 
                                    int meusOrcamentos = 0;
                                    for (int o = 0; o < orcamentoCliente.size(); o++) {
                                        if (orcamentoCliente.get(o) == clienteAtual) {
                                            meusOrcamentos++;
                                            System.out.println("Orçamento nº " + (o + 1) + " | vendedor " + orcamentoVendedor.get(o)
                                                + " | " + orcamentoStatus.get(o));
                                            mostrarOrcamento(o + 1, itemOrcamento, itemProduto, itemQtd,
                                                especificacoes, precos, porMetro, regime, orcamentoUf.get(o));
                                        }
                                    }
                                    if (meusOrcamentos == 0) {
                                        System.out.println("Você ainda não tem orçamentos.");
                                    }
                                    break;

                                case 2: 
                                    int numeroPagar = lerInteiro(entrada, "Número do orçamento que deseja pagar: ");
                                    if (numeroPagar < 1 || numeroPagar > orcamentoCliente.size()
                                        || orcamentoCliente.get(numeroPagar - 1) != clienteAtual) {
                                        System.out.println("[ERRO] Esse orçamento não existe ou não é seu.");
                                    } else if (!orcamentoStatus.get(numeroPagar - 1).equals("ABERTO")) {
                                        System.out.println("[ERRO] Esse orçamento está " + orcamentoStatus.get(numeroPagar - 1) + ".");
                                    } else {
                            
                                        double[] pedidoPorProduto = new double[capacidade];
                                        boolean temEstoque = true;
                                        for (int i = 0; i < itemOrcamento.size(); i++) {
                                            if (itemOrcamento.get(i) == numeroPagar) {
                                                int p = itemProduto.get(i);
                                                pedidoPorProduto[p] = arredondar(pedidoPorProduto[p] + itemQtd.get(i));
                                                if (pedidoPorProduto[p] > estoques[p]) {
                                                    temEstoque = false;
                                                }
                                            }
                                        }
                                        if (!temEstoque) {
                                            System.out.println("[ERRO] Algum item não tem mais estoque suficiente. Fale com o vendedor.");
                                        } else {
                                            String ufPagamento = orcamentoUf.get(numeroPagar - 1);
                                            double totalPagar = mostrarOrcamento(numeroPagar, itemOrcamento, itemProduto, itemQtd,
                                                especificacoes, precos, porMetro, regime, ufPagamento);
                                            int codigoForma = 0;
                                            while (formaPagamento(codigoForma).equals("")) {
                                                codigoForma = lerInteiro(entrada, "Forma de pagamento - 1 = Pix | 2 = Débito | 3 = Crédito | 4 = Boleto: ");
                                                if (formaPagamento(codigoForma).equals("")) {
                                                    System.out.println("[ERRO] Digite de 1 a 4.");
                                                }
                                            }
                                            int mesCompra = 0;
                                            while (!mesValido(mesCompra)) {
                                                mesCompra = lerInteiro(entrada, "Mês da compra, de 1 a 12: ");
                                                if (!mesValido(mesCompra)) {
                                                    System.out.println("[ERRO] O mês deve estar entre 1 e 12.");
                                                }
                                            }
                                            String confirmacao = "";
                                            while (!respostaValida(confirmacao)) {
                                                System.out.printf("Confirmar o pagamento de R$ %.2f com %s? S/N: ", totalPagar,
                                                    formaPagamento(codigoForma));
                                                confirmacao = entrada.nextLine();
                                                if (!respostaValida(confirmacao)) {
                                                    System.out.println("[ERRO] Responda S ou N.");
                                                }
                                            }
                                            if (!respostaSim(confirmacao)) {
                                                System.out.println("Pagamento não realizado. O orçamento continua ABERTO.");
                                            } else {
                                                String tipoNota = tipoDocumento(ufPagamento, true);
                                                int numeroNota;
                                                if (tipoNota.equals("NFC-e")) {
                                                    numeroNfce++;
                                                    numeroNota = numeroNfce;
                                                } else {
                                                    numeroNfe++;
                                                    numeroNota = numeroNfe;
                                                }
                                                String notaPagamento = tipoNota + " nº " + numeroNota;
                                                double aliquotaPagamento = aliquotaTotal(regime, aliquotaIcms(ufPagamento));
                                                for (int i = 0; i < itemOrcamento.size(); i++) {
                                                    if (itemOrcamento.get(i) == numeroPagar) {
                                                        int p = itemProduto.get(i);
                                                        double q = itemQtd.get(i);
                                                        double subtotalItem = calcularSubtotal(q, precos[p]);
                                                        double impostoItem = calcularImposto(subtotalItem, aliquotaPagamento);
                                                        double valorItem = calcularTotal(subtotalItem, impostoItem);
                                                        estoques[p] = arredondar(estoques[p] - q);
                                                        vendidos[p] = arredondar(vendidos[p] + q);
                                                        historicoQtd.add(q);
                                                        historicoValores.add(valorItem);
                                                        historicoImpostos.add(impostoItem);
                                                        historicoProduto.add(p);
                                                        historicoMes.add(mesCompra);
                                                        historicoUf.add(ufPagamento);
                                                        historicoDocumento.add(notaPagamento);
                                                        historicoVendedor.add(orcamentoVendedor.get(numeroPagar - 1));
                                                        historicoCliente.add(clienteAtual);
                                                        historicoPagamento.add(formaPagamento(codigoForma));
                                                        totalVendidoGeral = arredondar(totalVendidoGeral + valorItem);
                                                        totalImpostosGeral = arredondar(totalImpostosGeral + impostoItem);
                                                    }
                                                }
                                                orcamentoStatus.set(numeroPagar - 1, "PAGO");
                                                System.out.println("Pagamento aprovado! Obrigado pela compra.");
                                                System.out.println("========== " + notaPagamento + " ==========");
                                                System.out.println("Cliente: " + clienteNome.get(clienteAtual) + " | CPF/CNPJ: "
                                                    + clienteDocumento.get(clienteAtual));
                                                System.out.println("Destino: " + ufPagamento + " | Mês: " + mesCompra + " | Pagamento: "
                                                    + formaPagamento(codigoForma) + " | Vendedor: " + orcamentoVendedor.get(numeroPagar - 1));
                                                System.out.println("Tributos: " + descricaoTributos(regime, ufPagamento));
                                                System.out.printf("Total pago: R$ %.2f%n", totalPagar);
                                            }
                                        }
                                    }
                                    break;

                                case 3: 
                                    double totalMinhasCompras = 0;
                                    int minhasCompras = 0;
                                    for (int i = 0; i < historicoQtd.size(); i++) {
                                        if (historicoCliente.get(i) == clienteAtual) {
                                            int p = historicoProduto.get(i);
                                            System.out.printf("%s | " + formatoQtd(porMetro[p]) + " de %s | R$ %.2f | %s%n",
                                                historicoDocumento.get(i), historicoQtd.get(i), especificacoes[p], historicoValores.get(i),
                                                historicoPagamento.get(i));
                                            totalMinhasCompras = arredondar(totalMinhasCompras + historicoValores.get(i));
                                            minhasCompras++;
                                        }
                                    }
                                    if (minhasCompras == 0) {
                                        System.out.println("Você ainda não fez compras.");
                                    } else {
                                        System.out.printf("Total das suas compras: R$ %.2f%n", totalMinhasCompras);
                                    }
                                    break;

                                case 4:
                                    System.out.println("Deslogando...");
                                    break;

                                default:
                                    System.out.println("Opção inválida! Escolha entre 1 e 4.");
                            }
                        } while (opcao != 4);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Entrada encerrada. Sistema finalizado. Motivo: " + e);
        } finally {
            System.out.println("Sistema encerrado.");
            entrada.close();
        }
    }
}
