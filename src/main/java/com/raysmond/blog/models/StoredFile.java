package com.raysmond.blog.models;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "stored_files")
@Getter @Setter
public class StoredFile extends BaseModel {

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @Column(columnDefinition = "bigint default 0")
    private Long size;

    @Transient
    private String storagePath;

    public String getFullPath() {
        String separator = "";
        if (!StringUtils.isEmpty(storagePath) && !storagePath.endsWith("//")) {
            separator = "//";
        }
        return storagePath + separator + path;
    }

    public String getSizeFormatted() {
        double bytes = this.getSize();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double gigabytes = (megabytes / 1024);
        double terabytes = (gigabytes / 1024);
        double petabytes = (terabytes / 1024);
        double exabytes = (petabytes / 1024);
        double zettabytes = (exabytes / 1024);
        double yottabytes = (zettabytes / 1024);
        if (Math.floor(yottabytes) > 0d) {
            return String.format("%.3f Yb", yottabytes);
        } else if (Math.floor(zettabytes) > 0d) {
            return String.format("%.3f Zb", zettabytes);
        } else if (Math.floor(exabytes) > 0d) {
            return String.format("%.3f Eb", exabytes);
        } else if (Math.floor(petabytes) > 0d) {
            return String.format("%.3f Pb", petabytes);
        } else if (Math.floor(terabytes) > 0d) {
            return String.format("%.3f Tb", terabytes);
        } else if (Math.floor(gigabytes) > 0d) {
            return String.format("%.3f Gb", gigabytes);
        } else if (Math.floor(megabytes) > 0d) {
            return String.format("%.3f Mb", megabytes);
        } else if (Math.floor(kilobytes) > 0d) {
            return String.format("%.3f Kb", kilobytes);
        } else {
            return String.format("%d bytes", (int)bytes);
        }
    }

}
