package com.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createExpense_returns201_withCreatedExpense() throws Exception {
        String requestBody = """
            {
              "title": "Groceries",
              "amount": 45.30,
              "category": "Food",
              "date": "2026-06-05"
            }
            """;

        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(45.30))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.date").value("2026-06-05"));
    }

    @Test
    void createThenFindAll_includesCreatedExpense() throws Exception {
        String requestBody = """
            {
              "title": "Bus pass",
              "amount": 20.00,
              "category": "Transport",
              "date": "2026-06-06"
            }
            """;

        mockMvc.perform(post("/expenses")
                .contentType("application/json")
                .content(requestBody));

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title == 'Bus pass')]").exists());
    }

    @Test
    void createThenFindById_returnsExpense() throws Exception {
        String requestBody = """
            {
              "title": "Movie ticket",
              "amount": 12.50,
              "category": "Entertainment",
              "date": "2026-06-07"
            }
            """;

        String response = mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/expenses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Movie ticket"));
    }

    @Test
    void createThenDelete_returns204_andSubsequentGetReturns404() throws Exception {
        String requestBody = """
            {"title":"Coffee","amount":4.50,"category":"Food","date":"2026-06-08"}
            """;

        String response = mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/expenses/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/expenses/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void filterByCategory_returnsOnlyMatchingExpenses() throws Exception {
        mockMvc.perform(post("/expenses")
                .contentType("application/json")
                .content("""
                    {"title":"Groceries","amount":45.30,"category":"Food","date":"2026-06-05"}
                    """));
        mockMvc.perform(post("/expenses")
                .contentType("application/json")
                .content("""
                    {"title":"Bus pass","amount":20.00,"category":"Transport","date":"2026-06-06"}
                    """));

        mockMvc.perform(get("/expenses").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.category == 'Transport')]").doesNotExist());
    }

    @Test
    void createExpense_returns400_whenTitleIsBlank() throws Exception {
        String requestBody = """
            {"title":"   ","amount":10.00,"category":"Food","date":"2026-06-05"}
            """;

        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createExpense_returns400_whenAmountIsZeroOrNegative() throws Exception {
        String requestBody = """
            {"title":"Groceries","amount":0,"category":"Food","date":"2026-06-05"}
            """;

        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("amount")));
    }

    @Test
    void createExpense_returns400_withAllViolations_whenMultipleFieldsInvalid() throws Exception {
        String requestBody = """
            {"title":"","amount":-5,"category":"","date":"2026-06-05"}
            """;

        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("amount")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("category")));
    }
    @Test
    void monthlySummaryEndpoint_returnsGroupedTotals() throws Exception {
        mockMvc.perform(post("/expenses")
                .contentType("application/json")
                .content("""
                {"title":"Groceries","amount":45.30,"category":"Food","date":"2026-06-05"}
                """));

        mockMvc.perform(get("/expenses/summary/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['2026-06']").exists());
    }

    @Test
    void createExpense_returns400_onMalformedJson() throws Exception {
        String malformedBody = "{ this is not valid json ";

        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(malformedBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/expenses/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void delete_returns404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/expenses/nonexistent-id"))
                .andExpect(status().isNotFound());
    }
}