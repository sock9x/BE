package com.px.tool.domain.user.payload;

import com.px.tool.domain.user.Folder;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderPayload extends AbstractObject {
    private Long folderId;
    private String name;

    public static FolderPayload fromEntity(Folder folder) {
        FolderPayload payload = new FolderPayload();
        payload.folderId = folder.getFolderId();
        payload.name = folder.getName();
        return payload;
    }

    public Folder toEntity() {
        Folder folder = new Folder();
        folder.setFolderId(this.folderId);
        folder.setName(this.name);
        return folder;
    }

}
