/*
* Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
*/

import UIKit
import MultiPlatformLibrary
import Mapbox

class TestViewController: UIViewController {
    @IBOutlet var textLabel: UILabel!
    
    private var viewModel: TrackerViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let mapView = MGLMapView(frame: .zero)
        view = mapView
        
        viewModel = TrackerViewModel(
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
}
