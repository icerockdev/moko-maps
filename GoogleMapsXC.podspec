Pod::Spec.new do |s|
  s.name             = 'GoogleMapsXC'
  s.version          = "6.1.1-beta"
  s.summary          = 'GoogleMaps with xcframework'
  s.description      = 'GoogleMaps with xcframework description'
  s.homepage         = 'https://github.com/icerockdev/moko-maps'
  s.license          = { :type => 'UNKNOWN' }
  s.authors          = 'Google'
  s.source           = {
    :http => "https://dl.google.com/geosdk/GoogleMaps-#{s.version}-xcframework.tar.gz",
    :type => "tgz"
  }

  s.platform = :ios
  s.ios.deployment_target = '12.0'

  s.frameworks = ["Accelerate", "CoreData", "CoreGraphics", "CoreImage", "CoreLocation", "CoreTelephony", "CoreText", "GLKit", "ImageIO" "Metal", "OpenGLES", "QuartzCore", "SystemConfiguration", "UIKit"]
  s.libraries = ["c++", "z"]
  s.vendored_frameworks = [
    "GoogleMaps.xcframework",
    "GoogleMapsBase.xcframework",
    "GoogleMapsCore.xcframework"
  ]
end
