/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import GoogleMaps
import MultiPlatformLibrary
import UIKit

class GoogleMapViewController: UIViewController {
    
    private var viewModel: GoogleMapViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let mapView = GMSMapView(frame: .zero)
        view = mapView
        
        viewModel = GoogleMapViewModel(
            permissionsController: PermissionsController(),
            googleMapController: GoogleMapController(
                mapView: mapView,
                geoApiKey: "YOUR-API-KEY"
            )
        )
        
        viewModel.start()
    }
    
    deinit {
        viewModel.onCleared()
    }
    
}
