/*
* Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
*/

import UIKit
import MultiPlatformLibrary
import GoogleMaps

class TestViewController: UIViewController {
    @IBOutlet var textLabel: UILabel!
    
    private var viewModel: TrackerViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let camera = GMSCameraPosition.camera(
            withLatitude: -33.86,
            longitude: 151.20,
            zoom: 6.0
        )
        let mapView = GMSMapView.map(
            withFrame: CGRect.zero,
            camera: camera
        )
        view = mapView
        
        viewModel = TrackerViewModel(
            locationTracker: LocationTracker(
                permissionsController: PermissionsController(),
                accuracy: kCLLocationAccuracyBest
            ),
            mapsController: GoogleMapController(
                mapView: mapView,
                // TODO: Replace with your API Key from https://developers.google.com/maps/documentation/ios-sdk/
                geoApiKey: "YOUR-API-KEY"
            )
        )
        
        viewModel.start()
    }
}
