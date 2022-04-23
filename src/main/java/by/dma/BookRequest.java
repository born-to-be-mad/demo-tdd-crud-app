package by.dma;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookRequest {

  @NotEmpty
  @Size(max = 256)
  private String title;

  @NotEmpty
  @Size(max = 20)
  private String isbn;

  @NotEmpty
  @Size(max = 100)
  private String author;

  @Positive
  private double price;

  @NotEmpty
  @Min(1900)
  @Max(2100)
  private int publishYear;
}
