import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        try {
            // URL da API com a chave de API
            String url_str = "https://v6.exchangerate-api.com/v6/c29a5146084be70c53b97813/latest/USD";

            while (continuar) {
                // Fazendo a solicitação
                URL url = new URL(url_str);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                // Convertendo para JSON
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();

                // Acessando o objeto JSON
                String req_result = jsonobj.get("result").getAsString();
                System.out.println("Resultado da solicitação: " + req_result);

                // Obtendo as taxas de câmbio das moedas em relação ao BRL
                JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");
                double taxaBRL = rates.get("BRL").getAsDouble();

                // Mostrando os valores das moedas em tempo real convertidos para BRL em linha com destaque
                System.out.println("\nValores das moedas em tempo real (convertidos para BRL):");
                Map<String, String> nomeMoedas = new LinkedHashMap<>(); // Manter a ordem de inserção
                nomeMoedas.put("USD", "Dólar Americano (USD)");
                nomeMoedas.put("EUR", "Euro (EUR)");
                nomeMoedas.put("GBP", "Libra Esterlina (GBP)");
                nomeMoedas.put("JPY", "Iene Japonês (JPY)");
                nomeMoedas.put("CAD", "Dólar Canadense (CAD)");

                for (Map.Entry<String, String> entry : nomeMoedas.entrySet()) {
                    String moeda = entry.getKey();
                    double valor = rates.get(moeda).getAsDouble();
                    double valorEmBRL = valor * taxaBRL;
                    System.out.printf("%-20s %.4f BRL\n", entry.getValue() + ":", valorEmBRL);
                }

                // Moedas disponíveis para conversão
                List<String> moedas = new ArrayList<>(nomeMoedas.keySet());

                // Interagindo com o usuário para selecionar a moeda inicial
                System.out.println("\nEscolha a moeda inicial:");
                for (int i = 0; i < moedas.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, nomeMoedas.get(moedas.get(i)));
                }
                System.out.print("Digite o número da moeda inicial: ");
                int escolhaInicial = scanner.nextInt();

                // Verificando se a escolha inicial é válida
                if (escolhaInicial < 1 || escolhaInicial > moedas.size()) {
                    System.out.println("Escolha inicial inválida. Encerrando o programa.");
                    continuar = false;
                    break;
                }

                String moedaInicial = moedas.get(escolhaInicial - 1);

                // Interagindo com o usuário para selecionar a moeda de destino
                System.out.println("Escolha a moeda de destino:");
                for (int i = 0; i < moedas.size(); i++) {
                    if (i != escolhaInicial - 1) { // Evita mostrar a mesma moeda inicial na lista de destino
                        System.out.printf("%d. %s\n", i + 1, nomeMoedas.get(moedas.get(i)));
                    }
                }
                System.out.print("Digite o número da moeda de destino: ");
                int escolhaDestino = scanner.nextInt();

                // Verificando se a escolha de destino é válida
                if (escolhaDestino < 1 || escolhaDestino > moedas.size() || escolhaDestino == escolhaInicial) {
                    System.out.println("Escolha de destino inválida. Encerrando o programa.");
                    continuar = false;
                    break;
                }

                String moedaDestino = moedas.get(escolhaDestino - 1);

                // Obtendo a taxa de conversão para a moeda de destino escolhida
                double taxaConversao = rates.get(moedaDestino).getAsDouble();
                double quantidadeMoedaInicial = 1.0; // Quantidade fixa para a moeda inicial (1 unidade)

                // Calculando o valor convertido
                double valorConvertido = quantidadeMoedaInicial * taxaConversao;

                // Exibindo o resultado da conversão de forma mais clara e formatada
                System.out.printf("\nConvertendo %s para %s:\n", nomeMoedas.get(moedaInicial), nomeMoedas.get(moedaDestino));
                System.out.printf("Taxa de conversão: 1 %s = %.4f %s\n", nomeMoedas.get(moedaInicial), taxaConversao, nomeMoedas.get(moedaDestino));
                System.out.printf("Valor convertido: %.2f %s = %.2f %s\n", quantidadeMoedaInicial, nomeMoedas.get(moedaInicial), valorConvertido, nomeMoedas.get(moedaDestino));

                // Perguntando ao usuário se deseja continuar ou finalizar
                System.out.print("\nDeseja fazer outra conversão? (S/N): ");
                String resposta = scanner.next();
                if (!resposta.equalsIgnoreCase("S")) {
                    continuar = false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
