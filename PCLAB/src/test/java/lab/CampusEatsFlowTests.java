package lab;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CampusEatsFlowTests {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void orderDecrementsStock() throws Exception {
        String vendor = register("ROLE_VENDOR");
        String customer = register("ROLE_CUSTOMER");
        long storeId = createStore(vendor);
        long productId = createProduct(vendor, storeId, "4.50", 3);

        post("/api/orders", customer, order(storeId, productId, 3))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(13.50));

        mvc.perform(get("/api/products/" + productId))
                .andExpect(jsonPath("$.stock").value(0))
                .andExpect(jsonPath("$.status").value("SOLD_OUT"));

        post("/api/orders", customer, order(storeId, productId, 1))
                .andExpect(status().isConflict());
    }

    @Test
    void failedOrderRollsBack() throws Exception {
        String vendor = register("ROLE_VENDOR");
        String customer = register("ROLE_CUSTOMER");
        long storeId = createStore(vendor);
        long first = createProduct(vendor, storeId, "2.00", 10);
        long second = createProduct(vendor, storeId, "1.50", 1);

        post("/api/orders", customer, "{\"storeId\":" + storeId + ",\"items\":["
                + "{\"productId\":" + first + ",\"quantity\":5},"
                + "{\"productId\":" + second + ",\"quantity\":2}]}")
                .andExpect(status().isConflict());

        mvc.perform(get("/api/products/" + first)).andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void securityAndValidation() throws Exception {
        String customer = register("ROLE_CUSTOMER");
        String vendor = register("ROLE_VENDOR");
        String otherVendor = register("ROLE_VENDOR");
        long storeId = createStore(vendor);

        post("/api/stores", null, "{\"name\":\"X\"}").andExpect(status().isUnauthorized());
        post("/api/stores", customer, "{\"name\":\"X\"}").andExpect(status().isForbidden());
        post("/api/stores/" + storeId + "/products", otherVendor, "{\"name\":\"A\",\"price\":1,\"stock\":1}")
                .andExpect(status().isForbidden());

        post("/api/stores/" + storeId + "/products", vendor, "{\"name\":\"\",\"price\":0,\"stock\":-1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stock").exists());

        post("/api/auth/login", null, "{\"username\":\"nadie\",\"password\":\"incorrecta\"}")
                .andExpect(status().isUnauthorized());
    }

    private String register(String role) throws Exception {
        int n = SEQ.incrementAndGet();
        String body = "{\"username\":\"user" + n + "\",\"email\":\"user" + n
                + "@utec.edu.pe\",\"password\":\"Password123\",\"role\":\"" + role + "\"}";
        String response = post("/api/auth/register", null, body)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.token");
    }

    private long createStore(String token) throws Exception {
        String response = post("/api/stores", token, "{\"name\":\"Local " + SEQ.incrementAndGet() + "\"}")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private long createProduct(String token, long storeId, String price, int stock) throws Exception {
        String response = post("/api/stores/" + storeId + "/products", token,
                "{\"name\":\"Producto\",\"price\":" + price + ",\"stock\":" + stock + "}")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private String order(long storeId, long productId, int quantity) {
        return "{\"storeId\":" + storeId + ",\"items\":[{\"productId\":" + productId + ",\"quantity\":" + quantity + "}]}";
    }

    private ResultActions post(String url, String token, String json) throws Exception {
        var request = MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(json);
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(request);
    }
}
