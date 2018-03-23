package io.github.abhishekwl.electricsheepprimary.Models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

/**
 * Created by ghost on 3/22/18.
 */

public class Message implements IMessage {
    private String id, text;
    private IUser iUser;
    private Date createdDate;

    public Message(String id, String text, IUser iUser, Date createdDate) {
        this.id = id;
        this.text = text;
        this.iUser = iUser;
        this.createdDate = createdDate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return iUser;
    }

    @Override
    public Date getCreatedAt() {
        return new Date();
    }
}
