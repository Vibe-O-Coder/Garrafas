import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class Main {
    static Scanner scan = new Scanner(System.in);
    private static void removerGarrafas(List<Garrafa> lista, List<String> argumentos) {
        if (argumentos.size() > 1) {
            System.out.println("Uso: remover (ID)");
            return;
        }
        if (lista.isEmpty()) {
            System.out.println("Nenhuma garrafa registrada.");
            return;
        }
        ArrayList<Garrafa> novaLista = new ArrayList<>(lista);

        if (argumentos.isEmpty()) {
            System.out.print("Tem certeza? [S/n]: ");
            if (!Main.scan.hasNextLine()) {
                return;
            }
            String confirmar = Main.scan.nextLine().trim().toLowerCase();
            if (confirmar.equals("n")) {
                System.out.println("Operação cancelada.");
                return;
            } else if (confirmar.equals("s") || confirmar.equals("y")) {
                System.out.println("Operação confirmada.");
            } else {
                System.out.println("Escolha inválida, tente novamente. Por preocaução a operação foi cancelada automaticamente.");
                return;
            }
            novaLista.clear();
        } else {
            try {
                long idBuscado = Long.parseLong(argumentos.get(0));
                if (!novaLista.removeIf(garrafa -> garrafa.getID() == idBuscado)) {
                    System.out.println("Nenhuma garrafa encontrada.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("O ID deve ter um número inteiro.");
                return;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(novaLista);

        try {
            try (Writer writer = Files.newBufferedWriter(Path.of("garrafas.json"), StandardCharsets.UTF_8)) {
                writer.write(json);
            }

            lista.clear();
            lista.addAll(novaLista);

            System.out.println(argumentos.isEmpty() ?
                                "Todos os itens removidos com sucesso!"
                                : "Item removido com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }

    }
    private static void verGarrafas(List<Garrafa> lista, List<String> argumentos) {
        if (argumentos.isEmpty()) {
            if (lista.isEmpty()) {
                System.out.println("Nenhuma garrafa registrada.");
                return;
            }

            for (Garrafa garrafa : lista) {
                System.out.println(garrafa);
            }

            return;
        }

        if (argumentos.size() != 2) {
            System.out.println("""
                    Formato incorreto. Utilize o formato fornecido:
                    ver (tipoReferencia) (referencia)
                    Ex: ver id 12
                Mostra a garrafa de ID 12 no banco
                    ver marca coca_cola
                Mostra as garrafas com a marca "coca cola"
                    """);
            return;
        }

        String campo = argumentos.get(0).toLowerCase();
        String valor = argumentos.get(1).replace("_", " ");

        if (!List.of("id", "marca", "conteudo", "capacidade", "preco").contains(campo)) {
            System.out.println("Busque por id, marca, conteudo, capacidade ou preco");
            return;
        }

        long idBuscado = -1;
        double capacidadeBuscada = -1;
        long precoBuscado = -1;

        try {
            if (campo.equals("id")) {
                idBuscado = Long.parseLong(valor);
            }
            if (campo.equals("capacidade")) {
                capacidadeBuscada = Double.parseDouble(valor);
            }
            if (campo.equals("preco")) {
                precoBuscado = Long.parseLong(valor);
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor número inválido. Utilize ponto nos decimais.");
            return;
        }

        boolean encontrou = false;

        for (Garrafa garrafa : lista) {
            boolean corresponde;

            switch (campo) {
                case "id":
                    corresponde = garrafa.getID() == idBuscado;
                    break;
                case "marca":
                    corresponde = garrafa.getMarca().equalsIgnoreCase(valor);
                    break;
                case "conteudo":
                    corresponde = garrafa.getConteudo().equalsIgnoreCase(valor);
                    break;
                case "capacidade":
                    corresponde = garrafa.getCapacidade() == capacidadeBuscada;
                    break;
                case "preco":
                    corresponde = garrafa.getPreco() == precoBuscado;
                    break;
                default:
                    corresponde = false;
                    break;
            }
            if (corresponde) {
                System.out.println(garrafa);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhuma garrafa encontrada.");
        }

    }

    public static void mostrarAjuda() {
        System.out.println("""
                Banco de dados de garrafas inspirado no Axxis
                Os comandos são separados por espaço (Ex: comando1 comando2)
                Se algo escrito como a marca ou o conteúdo da garrafa tiver espaços, utilize '_' no lugar
                Os preços devem ser adicionados em centavos, mas o banco converte para reais automaticamente

                Comandos (placeholders com "<>" são obrigatórios e com "()" são opcionais):

                criar -> Cria uma garrafa no banco de dados:
                criar <marca> <quantidade> <capacidade ML> <conteudo> <preçoCentavos>
                Ex: criar Coca_Cola 10 500 Coca 500
                Cria uma garrafa da marca Coca Cola com 10 no estoque, com a capacidade de 500ML, tendo o conteúdo Coca e o preço de 500 centavos (5 reais).

                remover -> Remove uma garrafa do banco de dados (por segurança a remoção funciona somente por ID):
                remover (ID)
                Ex: remover
                Limpa o banco de dados após confirmação.
                    remover 12
                Remove a garrafa de ID 12 do banco de dados.

                mudar -> Muda informações de uma garrafa do banco de dados:
                mudar <ID> <referencia> <novoValor>
                Ex: mudar 12 preco 1000
                Muda o preço da garrafa de ID 12 para 1000 centavos (R$10,00)

                ver -> Mostra as informações de todas as garrafas ou de apenas uma em específico:
                ver (tipoReferencia) (referencia)
                Ex: ver id 12
                Mostra a garrafa de ID 12 no banco
                    ver marca coca_cola
                Mostra as garrafas com a marca "coca cola"

                ajuda -> mostra essa mensagem

                limpar -> limpa o terminal
                """);
    }


    public static void main(String[] args) {
        ArrayList<Garrafa> listaGarrafa = new ArrayList<>();
        try {
            listaGarrafa = Garrafa.carregar();

            System.out.println(listaGarrafa.size() + " registro(s) carregado(s).");

        } catch (IOException | JsonParseException | IllegalStateException e) {
            System.out.println("Erro ao carregar as garrafas: " + e.getMessage());
            return;
        }
        mostrarAjuda();
        while (true) { 
            System.out.print(">> ");
            if (!Main.scan.hasNextLine()) {
                break;
            }
            String comando = Main.scan.nextLine().trim();
            if (comando.isEmpty()) {
                continue;
            }
            ArrayList<String> comandoList = new ArrayList<>(List.of(comando.split("\\s+")));
            String comandoMain = comandoList.remove(0).toLowerCase();
            if (comandoMain.equals("sair")) {
                System.out.println("Fechando programa...");
                break;
            }
            switch (comandoMain) {
                case "criar":
                    if (comandoList.size() != 5) {
                        System.out.println("""
                                Uso:
                                criar <marca> <quantidade> <capacidadeML> <conteudo> <preçoCentavos>
                                """);
                        break;
                    }
                    try {
                        String marca = comandoList.get(0).replace("_", " ");
                        int quantidade = Integer.parseInt(comandoList.get(1));
                        double capacidade = Double.parseDouble(comandoList.get(2));
                        String conteudo = comandoList.get(3).replace("_", " ");
                        long precoCentavos = Long.parseLong(comandoList.get(4));
    
                        Garrafa garrafa = new Garrafa(marca, quantidade, capacidade, conteudo, precoCentavos);
                        String resultado = garrafa.salvar();
                        if (resultado.equals("Garrafa adicionada com sucesso!")) {
                            listaGarrafa.add(garrafa);
                        }
                        System.out.println(resultado);
                    } catch (NumberFormatException e) {
                        System.out.println("Quantidade e preço devem ser números inteiros. Para capacidade decimal, utilize ponto.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "ver":
                    verGarrafas(listaGarrafa, comandoList);
                    break;
                case "remover":
                    removerGarrafas(listaGarrafa, comandoList);
                    break;
                case "mudar":
                    if (comandoList.size() != 3) {
                        System.out.println("Uso: mudar <ID> <referencia> <novoValor>");
                        break;
                    }
                    try {
                        long idBuscado = Long.parseLong(comandoList.get(0));
                        Garrafa garrafaBuscada = null;
                        for (Garrafa garrafa : listaGarrafa) {
                            if (garrafa.getID() == idBuscado) {
                                garrafaBuscada = garrafa;
                                break;
                            }
                        }
                        if (garrafaBuscada == null) {
                            System.out.println("Nenhuma garrafa encontrada.");
                            break;
                        }
                        Gson gson = new Gson();
                        Garrafa alterada = gson.fromJson(gson.toJson(garrafaBuscada), Garrafa.class);
                        String referencia = comandoList.get(1);
                        String valor = comandoList.get(2).replace("_", " ");
                        String resultado = null;
                        switch (referencia) {
                            case "marca":
                                resultado = alterada.setMarca(valor);
                                break;
                            case "quantidade":
                                resultado = alterada.setQuantidade(Integer.parseInt(valor));
                                break;
                            case "capacidade":
                                resultado = alterada.setCapacidade(Double.parseDouble(valor));
                                break;
                            case "conteudo":
                                resultado = alterada.setConteudo(valor);
                                break;
                            case "preco":
                                resultado = alterada.setPreco(Long.parseLong(valor));
                                break;
                            default:
                                System.out.println("Referência inválida.");
                                break;
                        }
                        if (resultado == null) {
                            break;
                        }

                        if (resultado.startsWith("Inválido:")) {
                            System.out.println(resultado);
                            break;
                        }

                        String gravacao = alterada.salvar();

                        if (gravacao.equals("Garrafa adicionada com sucesso!")) {
                            listaGarrafa.set(listaGarrafa.indexOf(garrafaBuscada), alterada);
                            System.out.println("Garrafa mudada com sucesso!");
                        } else {
                            System.out.println(gravacao);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ID, quantidade e preço devem ser inteiros. Use ponto na capacidade decimal.");
                    }
                    break;
                case "ajuda":
                    mostrarAjuda();
                    break;
                case "limpar":
                    System.out.print("\033[H\033[2J");  
                    System.out.flush();
                    break;
                default:
                    System.out.println("Comando inválido.");
                    break;
            }
        }
        Main.scan.close();
    }
}