//
//  ViewController.swift
//  local_notification_test
//
//  Created by Shi Zhang on 11/20/17.
//  Copyright © 2017 Shi Zhang. All rights reserved.
//

import UIKit
import UserNotifications


class ViewController: UIViewController {
    var timeEntered: String = "";
    var trigger: UNTimeIntervalNotificationTrigger? = nil;
    var repeatNotifications: Bool = true
    @IBOutlet var time: UITextField!;
    @IBAction func setNotification(_ sender: Any) {
//        timeEntered = time.text!
//        let hrMin = timeEntered.components(separatedBy: ":");
        let content = UNMutableNotificationContent()
        content.title = "How many days are there in one year"
        content.subtitle = "Do you know?"
        content.body = "Do you really know?"
        content.badge = 1
        content.sound = UNNotificationSound(named: "alarm.aiff");
    
        while (repeatNotifications) {
            trigger = UNTimeIntervalNotificationTrigger(timeInterval: 5, repeats: false);
            
            let request = UNNotificationRequest(identifier: "timerDone", content: content, trigger: trigger)
            
            UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
        }
    }
    
    @IBAction func stopRepeat(_ sender: Any) {
        repeatNotifications = false
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge], completionHandler: {didAllow, error in
        })

    }

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

