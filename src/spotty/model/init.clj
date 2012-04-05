(ns spotty.model.init
  (:require
   [appengine-magic.services.datastore :as ds]
   [spotty.model.channel :as channel]
   [spotty.model.member :as member]))

(defn init []
  (let [henk (member/create "DJ Henk" "henkpunt@gmail.com")
        boris (member/create "DJ Boris" "boris@hyves.nl")]
    (channel/create henk "MetalliChannel" "Seek & Destroy!"
                    "http://ticketsthere.files.wordpress.com/2010/07/metallica.jpg")
    (channel/create boris "Veronico" "Roepi! Roepi!"
                    "http://www.shownieuws.tv/wp-content/uploads/shownieuws/import/2008/12/09/veronica-545x306.jpg")
    (channel/create henk "Slayor!" "Slayooohr!"
                    "http://wallpapers-diq.com/wallpapers/84/Slayer_-_Heavy_Metal_Band.jpg")
    (channel/create boris "24 hr Disco!" "Where Disco Lives Forever!"
                    "http://www.digidact.nl/Digidact_C01/UploadData/images/49/0/Magda/disco.jpg")))



