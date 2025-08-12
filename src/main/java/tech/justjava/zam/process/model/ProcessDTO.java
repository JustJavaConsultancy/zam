package tech.justjava.zam.process.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.justjava.zam.file.model.FileData;
import tech.justjava.zam.file.model.ValidFileType;


@Getter
@Setter
public class ProcessDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @ProcessModelIdUnique
    private String modelId;

    @NotNull
    @Size(max = 255)
    @ProcessProcessNameUnique
    private String processName;

    @Valid
    @ValidFileType({"jpeg", "png", "gif"})
    private FileData diagram;

}
