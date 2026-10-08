import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Garrafa {
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private String marca;
    private int quantidade;
    private double capacidade;
    private String conteudo;
    private long preco;
    private final long ID;

    public Garrafa(String marca, int quantidade, double capacidade, String conteudo, long preco) {
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("Marca não fornecida");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        this.marca       =  marca;
        this.quantidade  =  quantidade;
        this.capacidade  =  capacidade;
        this.conteudo    =  conteudo;
        this.preco       =  preco;
        this.ID          =  ID_GENERATOR.getAndIncrement();
    }
    public String getMarca() {
        return this.marca;
    }
    public int getQuantidade() {
        return this.quantidade;
    }
    public double getCapacidade() {
        return this.capacidade;
    }
    public String getConteudo() {
        return this.conteudo;
    }
    public double getPreco() {
        return this.preco;
    }
    public long getID() {
        return this.ID;
    }
    
    public String setMarca(String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            return "Inválido: marca não fornecida.";
        }
        this.marca = marca;
        return "Marca registrada com sucesso!";
    }
    public String setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            return "Inválido: a quantidade deve ser maior que zero.";
        }
        this.quantidade = quantidade;
        return "Quantidade registrada com sucesso!";

    }
    public String setCapacidade(double capacidade) {
        if (capacidade <= 0) {
            return "Inválido: capacidade deve ser maior que zero.";
        }
        this.capacidade = capacidade;
        return "Capacidade registrada com sucesso!";
    }
    public String setConteudo(String conteudo) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            return "Inválido: conteúdo não fornecido.";
        }
        this.conteudo = conteudo;
        return "Conteúdo registrado com sucesso!";
    }
    public String setPreco(long preco) {
        if (preco <= 0.0) {
            return "Inválido: preço deve ser maior que zero.";
        } 
        this.preco = preco;
        return "Preço registrado com sucesso!";
    }
    @Override 
    public String toString() {
        int reais = (int) (this.preco/100);
        int centavos = (int) (this.preco%100);
        String total = String.format("R$ %d,%-2d", reais, centavos);
        double litro = this.capacidade/1000;
        String info = "ID: " + this.ID + "\n"
                    + "Marca: " + this.marca + "\n"
                    + "Conteúdo: " + this.conteudo + "\n"
                    + "Preço: " + total + "\n"
                    + "Quantidade: " + this.quantidade + "\n"
                    + "Capacidade: " + this.capacidade + "ml " + String.format("(%.1f L)\n", litro);
        return info;
    }

    public String salvar() {
        Path caminho = Path.of("garrafas.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            JsonArray garrafas;

            // recupera uma lista já existente ou cria uma nova

            if (Files.exists(caminho) && Files.size(caminho) > 0) {
                try (Reader leitor  = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
                    garrafas = JsonParser.parseReader(leitor).getAsJsonArray();
                }
            } else {
                garrafas = new JsonArray();
            }

            // converte esta garrafa em Json e adiciona à lista
            garrafas.add(gson.toJsonTree(this));

            // Monta o texto antes de abrir o arquivo para escrita
            String json = gson.toJson(garrafas);

            try (Writer arquivo = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
                arquivo.write(json);
            }
            return "Garrafa adicionada com sucesso!";
        } catch (IOException e) {
            return "Erro ao acessar o arquivo: " + e.getMessage();
        } catch (JsonParseException | IllegalStateException e) {
            return "O arquivo precisa conter um array JSON válido";
        }
    }
}