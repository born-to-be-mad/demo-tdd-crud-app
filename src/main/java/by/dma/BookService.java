package by.dma;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Long createNewBook(BookRequest bookRequest) {
    Book book = new Book();
    book.setIsbn(bookRequest.getIsbn());
    book.setAuthor(bookRequest.getAuthor());
    book.setTitle(bookRequest.getTitle());
    book.setPublishYear(bookRequest.getPublishYear());
    book.setPrice(bookRequest.getPrice());

    book = bookRepository.save(book);

    return book.getId();
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  public Book getBookById(Long id) {
    Optional<Book> requestedBook = bookRepository.findById(id);

    if (requestedBook.isEmpty()) {
      throw BookNotFoundException.fromId(id);
    }

    return requestedBook.get();
  }

  @Transactional
  public Book updateBook(Long id, BookRequest bookToUpdateRequest) {
    Optional<Book> bookFromDatabase = bookRepository.findById(id);

    if (bookFromDatabase.isEmpty()) {
      throw BookNotFoundException.fromId(id);
    }

    Book bookToUpdate = bookFromDatabase.get();
    bookToUpdate.setAuthor(bookToUpdateRequest.getAuthor());
    bookToUpdate.setIsbn(bookToUpdateRequest.getIsbn());
    bookToUpdate.setTitle(bookToUpdateRequest.getTitle());
    bookToUpdate.setIsbn(bookToUpdateRequest.getIsbn());
    bookToUpdate.setPublishYear(bookToUpdateRequest.getPublishYear());

    return bookToUpdate;
  }

  public void deleteBookById(Long id) {
    bookRepository.deleteById(id);
  }
}
