package br.com.samuellima.qa.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.samuellima.qa.base.BaseTest;
import br.com.samuellima.qa.pages.SearchResultsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Busca no blog")
class SearchTest extends BaseTest {

    private static final String TERMO_COM_RESULTADO = "crédito";
    private static final String TERMO_SEM_RESULTADO = "xablau";

    @Test
    @Story("Busca com resultados")
    @DisplayName("Deve retornar artigos relevantes ao buscar um termo existente")
    @Description("Busca via URL por um termo existente e valida que há resultados, "
            + "que o título da página reflete o termo, e que há relevância nos "
            + "títulos retornados.")
    void deveRetornarArtigosRelevantes() {
        SearchResultsPage results = new SearchResultsPage(page())
                .searchByUrl(TERMO_COM_RESULTADO);

        assertTrue(results.headingText().toLowerCase().contains(TERMO_COM_RESULTADO),
                "O título da página deveria refletir o termo buscado");

        List<String> titulos = results.resultTitles();

        assertFalse(titulos.isEmpty(),
                "Deveria haver ao menos um resultado para o termo");

        assertTrue(titulos.stream().anyMatch(t -> t.toLowerCase().contains(TERMO_COM_RESULTADO)),
                "Ao menos um título deveria conter o termo buscado. Títulos: " + titulos);
    }

    @Test
    @Story("Busca sem resultados")
    @DisplayName("Deve exibir mensagem amigável ao buscar um termo inexistente")
    @Description("Busca via URL por um termo improvável e valida o tratamento "
            + "gracioso do vazio: mensagem de 'nada encontrado' e ausência de artigos.")
    void deveExibirMensagemQuandoNaoHaResultados() {
        SearchResultsPage results = new SearchResultsPage(page())
                .searchByUrl(TERMO_SEM_RESULTADO);

        assertTrue(results.isEmptyMessageVisible(),
                "Deveria exibir a mensagem de nenhum resultado encontrado");

        assertEquals(0, results.resultsCount(),
                "Não deveria haver artigos para um termo inexistente");
    }

    @Test
    @Story("Busca via campo da página de resultados")
    @DisplayName("Deve permitir nova busca pelo campo funcional após um resultado vazio")
    @Description("Entra na página sem resultados (onde o campo de busca é funcional, "
            + "pois o JS já foi carregado) e realiza uma nova busca por interação real, "
            + "validando que passa a retornar resultados.")
    void devePermitirNovaBuscaPeloCampoFuncional() {
        SearchResultsPage results = new SearchResultsPage(page())
                .searchByUrl(TERMO_SEM_RESULTADO)
                .searchByField(TERMO_COM_RESULTADO);

        assertTrue(results.headingText().toLowerCase().contains(TERMO_COM_RESULTADO),
                "Após a nova busca, o título deveria refletir o novo termo");

        assertTrue(results.resultsCount() > 0,
                "A busca pelo campo deveria retornar resultados");

        assertFalse(results.isEmptyMessageVisible(),
                "Não deveria mais exibir a mensagem de vazio");
    }
}