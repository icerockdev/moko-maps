/*
* Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
*/

import Mapbox
import MultiPlatformLibrary
import UIKit

class MapboxViewController: UIViewController {
    private var viewModel: MapboxViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let mapView = MGLMapView(frame: .zero)
        view = mapView
        
        viewModel = MapboxViewModel(
            locationTracker: LocationTracker(
                permissionsController: PermissionsController(),
                accuracy: kCLLocationAccuracyBest
            ),
            mapsController: MapboxController(
                mapView: mapView
            )
        )
        
        viewModel.start()
    }
    
    deinit {
        viewModel.onCleared()
    }
}
