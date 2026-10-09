import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
            throw new IllegalArgumentException("Marca não fornecida.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (!Double.isFinite(capacidade) || capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo não fornecido.");
        }
        if (ID_GENERATOR.get() == Long.MAX_VALUE) {
            throw new IllegalArgumentException("Número máximo de IDs atingido.");
        }
        this.marca       =  marca;
        this.quantidade  =  quantidade;
        this.capacidade  =  capacidade;
        this.conteudo    =  conteudo;
        this.preco       =  preco;
        this.ID          =  ID_GENERATOR.getAndIncrement();
    }
    // construtor privado utilizado pelo Gson ao recuperar as garrafas do JSON
    private Garrafa() {
        this.ID = -1;
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
    public long getPreco() {
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
        if (!Double.isFinite(capacidade) || capacidade <= 0) {
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
        long reais      = this.preco/100;
        long centavos   = this.preco%100;
        String total    = String.format("R$ %d,%02d", reais, centavos);
        double litro    = this.capacidade/1000;
        String info     = "ID: " + this.ID + "\n"
                        + "Marca: " + this.marca + "\n"
                        + "Conteúdo: " + this.conteudo + "\n"
                        + "Preço: " + total + "\n"
                        + "Quantidade: " + this.quantidade + "\n"
                        + "Capacidade: " + this.capacidade + "ml " + String.format("(%.1f L)\n", litro);
        return info;
    }

    public String salvar() {
        Path caminho = Path.of("garrafas.json");
        Gson gson    = new GsonBuilder().setPrettyPrinting().create();

        try {
            ArrayList<Garrafa> garrafas = carregar();
            boolean encontrada = false;
            for (int i = 0; i < garrafas.size(); i++) {
                if (garrafas.get(i).getID() == this.ID) {
                    garrafas.set(i, this);
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                garrafas.add(this);
            }
            String json = gson.toJson(garrafas);

            try (Writer arquivo = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
                arquivo.write(json);
            }
            return "Garrafa adicionada com sucesso!";
        } catch (IOException e) {
            return "Erro ao acessar o arquivo: " + e.getMessage();
        } catch (JsonParseException | IllegalStateException e) {
            return "Erro no arquivo JSON:" + e.getMessage();
        }
    }

    public static ArrayList<Garrafa> carregar() throws IOException {
        Path caminho = Path.of("garrafas.json");
        ArrayList<Garrafa> lista = new ArrayList<>();

        if (!Files.exists(caminho) || Files.size(caminho) == 0) {
            return lista;
        }

        Gson gson = new Gson();
        Garrafa[] carregadas;

        try (Reader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            carregadas = gson.fromJson(leitor, Garrafa[].class);
        }
        if (carregadas == null) {
            throw new JsonParseException("O arquivo deve conter um array JSON.");
        }

        long maiorID = -1;
        Set<Long> idsEncontrados = new HashSet<>();

        for (Garrafa garrafa : carregadas) {
            if (garrafa == null || garrafa.ID < 0 || garrafa.ID == Long.MAX_VALUE) {
                throw new JsonParseException("Garrafa com ID inválido");
            }

            if (!idsEncontrados.add(garrafa.ID)) {
                throw new JsonParseException("ID repetido: " + garrafa.ID);
            }
            if (garrafa.marca == null || garrafa.marca.trim().isEmpty()
            || garrafa.conteudo == null || garrafa.conteudo.trim().isEmpty()
            || garrafa.quantidade <= 0
            || !Double.isFinite(garrafa.capacidade)
            || garrafa.capacidade <= 0
            || garrafa.preco <= 0) {
                throw new JsonParseException(
                "Dados inválidos na garrafa de ID " + garrafa.ID);
            }

            lista.add(garrafa);
            maiorID = Math.max(maiorID, garrafa.ID);

            ID_GENERATOR.set(Math.max(ID_GENERATOR.get(), maiorID + 1));

        }
        return lista;
    }
}