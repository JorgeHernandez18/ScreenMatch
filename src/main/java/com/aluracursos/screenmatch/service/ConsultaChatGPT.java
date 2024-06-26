package com.aluracursos.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    public static String obtenerTraduccion(String texto){
        OpenAiService service = new OpenAiService("");
        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("Por favor, traduce al español el siguiente texto: ")
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var respuesta = service.createCompletion(request);
        return respuesta.getChoices().get(0).getText();
    }
}
