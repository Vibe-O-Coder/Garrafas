import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<Garrafa> listaGarrafa = new ArrayList<>();
        System.out.println("""
                Banco de dados de garrafas inspirado no Axxis
                Os comandos são separados por espaço (Ex: comando1 comando2)
                Se algo escrito como a marca ou o conteúdo da garrafa tiver espaços, utilize '_' no lugar
                Os preços devem ser adicionados em centavos, mas o banco converte para reais automaticamente

                Comandos (placeholders com "<>" são obrigatórios e com "()" são opcionais):
                criar -> criar <marca> <quantidade> <capacidade ML> <conteudo> <preçoCentavos>
                Ex: criar Coca-Cola 10 500 Coca 500

                remover

                mudar

                ver\n
                """);
        while (true) { 
            System.out.print(">> ");
            String comando = scan.nextLine().trim();
            if (comando.isEmpty()) {
                continue;
            }
            ArrayList<String> comandoList = new ArrayList<>(List.of(comando.split("\\s+")));
            String comandoMain = comandoList.remove(0);
            switch (comandoMain) {
                case "criar":
                    if (comandoList.size() != 5) {
                        System.out.println("""
                                Uso:
                                criar <marca> <quantidade> <capacidadeML> <conteudo> <preçoCentavos>
                                """);
                    }
                    try {
                        String marca = comandoList.get(0).replace("_", " ");
                        int quantidade = Integer.parseInt(comandoList.get(1));
                        double capacidade = Double.parseDouble(comandoList.get(2));
                        String conteudo = comandoList.get(3).replace("_", " ");
                        int preco = Integer.parseInt(comandoList.get(4));
    
                        Garrafa garrafa = new Garrafa(marca, quantidade, capacidade, conteudo, preco);
                        listaGarrafa.add(garrafa);
                        System.out.println("Garrafa adicionada com sucesso!");
                    } catch (NumberFormatException e) {
                        System.out.println("Quantidade e preço devem ser números inteiros. Para capacidade decimal, utilize ponto.");
                    }
                case "remover":
                    
                default:
                    throw new AssertionError();
            }

            break;
        }
        scan.close();
    }
}