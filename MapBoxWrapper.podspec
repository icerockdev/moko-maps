Pod::Spec.new do |s|
  s.name             = 'MapBoxWrapper'
  s.version          = "10.4.1"
  s.summary          = 'GoogleMaps with xcframework'
  s.description      = 'GoogleMaps with xcframework description'
  s.homepage         = 'https://github.com/icerockdev/moko-maps'
  s.license          = { :type => 'UNKNOWN' }
  s.authors          = 'Google'
  s.source           = {
    :http => "none",
    :type => "tgz"
  }

  s.platform = :ios
  s.ios.deployment_target = '12.0'

  s.dependency 'MapboxMaps', "#{s.version}"
  s.source_files = 'maps-mapbox/src/iosMain/swift/*.swift'
end
