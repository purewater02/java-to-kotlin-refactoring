package com.group.libraryapp.dto.book.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookReturnRequest {

  private String userName;
  private String bookName;

}
