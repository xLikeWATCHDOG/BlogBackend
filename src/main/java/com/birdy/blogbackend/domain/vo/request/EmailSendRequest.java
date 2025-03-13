package com.birdy.blogbackend.domain.vo.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author birdy
 */
@Data
public class EmailSendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
}
