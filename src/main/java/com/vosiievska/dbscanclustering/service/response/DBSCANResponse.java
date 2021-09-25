package com.vosiievska.dbscanclustering.service.response;

import com.vosiievska.dbscanclustering.entity.Vehicle;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DBSCANResponse {

    private int clustersNumber;
    private List<Vehicle> partOfCluster;
    private List<Vehicle> noise;

}
