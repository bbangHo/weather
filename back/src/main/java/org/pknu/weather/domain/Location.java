package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    private Long id;

    @Column(name = "POINT", columnDefinition = "POINT SRID 4326", nullable = false)
    private Point point;

    private Double longitude;

    private Double latitude;

    private String province;

    private String city;

    private String street;

    @Builder.Default
    @OneToMany(mappedBy = "location",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Weather> weatherList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "location",
            fetch = FetchType.LAZY)
    private List<Tag> TagList = new ArrayList<>();

    public String getAddress() {
        return getCity() + getStreet();
    }

    public String getFullAddress() {
        return getProvince() + getCity() + getStreet();
    }
}
