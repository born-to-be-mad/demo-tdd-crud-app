package by.dma;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookservice;

    @Captor
    private ArgumentCaptor<BookRequest> requestArgumentCaptor;

    @Test
    public void postingANewBookShouldCreateANewBook() throws Exception {
        // given
        var isbn = "123456789";
        var title = "Jedi way to Java 17";
        var author = "James Bond";
        var price = 29.99;
        var publishYear = 2022;
        BookRequest bookRequest = BookRequest.builder()
                .author(author)
                .isbn(isbn)
                .title(title)
                .price(price)
                .publishYear(publishYear).build();

        when(bookservice.createNewBook(requestArgumentCaptor.capture())).thenReturn(1L);

        // when
        var resultActions = mockMvc
                .perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/books/1"));

        var captorValue = requestArgumentCaptor.getValue();
        assertThat(captorValue.getAuthor(), is(author));
        assertThat(captorValue.getIsbn(), is(isbn));
        assertThat(captorValue.getTitle(), is(title));
        assertThat(captorValue.getAuthor(), is(author));
        assertThat(captorValue.getPrice(), is(price));

    }

    @Test
    public void allBooksEndpointShouldReturnTwoBooks() throws Exception {

        when(bookservice.getAllBooks())
                .thenReturn(List.of(
                        new Book()
                                .setAuthor("James Bond")
                                .setIsbn("123456")
                                .setTitle("Jedi way to Java 17")
                                .setId(1L),
                        new Book()
                                .setAuthor("John Snow")
                                .setIsbn("234567")
                                .setTitle("Java EE 8")
                                .setId(2L))
                );

        mockMvc
                .perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Jedi way to Java 17")))
                .andExpect(jsonPath("$[0].author", is("James Bond")))
                .andExpect(jsonPath("$[0].isbn", is("123456")))
                .andExpect(jsonPath("$[0].id", is(1)));

    }

    @Test
    public void getBookWithIdOneShouldReturnABook() throws Exception {

        when(bookservice.getBookById(1L))
                .thenReturn(new Book()
                        .setAuthor("James Bond")
                        .setIsbn("123456")
                        .setTitle("Jedi way to Java 17")
                        .setId(1L));

        mockMvc
                .perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title", is("Jedi way to Java 17")))
                .andExpect(jsonPath("$.author", is("James Bond")))
                .andExpect(jsonPath("$.isbn", is("123456")))
                .andExpect(jsonPath("$.id", is(1)));

    }

    @Test
    public void getBookWithUnknownIdShouldReturn404() throws Exception {
        when(bookservice.getBookById(1L))
                .thenThrow(new BookNotFoundException("Book with id '1' not found"));

        mockMvc
                .perform(get("/api/books/1"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void updateBookWithKnownIdShouldUpdateTheBook() throws Exception {
        var author = "James Bond";
        var isbn = "123456";
        var title = "Java 17";
        var price = 29.99;
        var publishYear = 2022;
        BookRequest bookRequest = BookRequest.builder()
                .author(author)
                .isbn(isbn)
                .title(title)
                .price(price)
                .publishYear(publishYear)
                .build();

        when(bookservice.updateBook(eq(1L), requestArgumentCaptor.capture()))
                .thenReturn(new Book()
                        .setPublishYear(publishYear)
                        .setPrice(price)
                        .setAuthor(author)
                        .setIsbn(isbn)
                        .setTitle(title)
                        .setId(1L));

        mockMvc
                .perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.publishYear", is(publishYear)))
                .andExpect(jsonPath("$.price", is(price)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.author", is(author)))
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.id", is(1)));

        var requestArgumentCaptorValue = requestArgumentCaptor.getValue();
        assertThat(requestArgumentCaptorValue.getPublishYear(), is(publishYear));
        assertThat(requestArgumentCaptorValue.getPrice(), is(price));
        assertThat(requestArgumentCaptorValue.getAuthor(), is(author));
        assertThat(requestArgumentCaptorValue.getIsbn(), is(isbn));
        assertThat(requestArgumentCaptorValue.getTitle(), is(title));

    }

    @Test
    public void updateBookWithUnknownIdShouldReturn404() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
                .author("James Bond")
                .isbn("123456")
                .title("Java 17")
                .price(29.99)
                .publishYear(2022)
                .build();

        when(bookservice.updateBook(eq(42L), requestArgumentCaptor.capture()))
                .thenThrow(new BookNotFoundException("The book with id '42' was not found"));

        mockMvc
                .perform(put("/api/books/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound());

    }

}
