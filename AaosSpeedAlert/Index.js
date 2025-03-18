const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
admin.initializeApp();

exports.speedLimitExceeded = functions.firestore
    .document('speed-exceeded/{documentId}')  // Listen for new speed violations
    .onCreate(async (snap, context) => {
        const data = snap.data();
        const speed = data.speed;
        const limit = data.limit;
        const renterId = data.renterId;
        const userToken = data.userToken;
        const latitude = data.latitude;
        const longitude = data.longitude;

        console.log(`⚠️ Speed limit exceeded by renter ${renterId}`);
        console.log(`📍 Location: (${latitude}, ${longitude})`);
        console.log(`🚗 Speed: ${speed} km/h (Limit: ${limit} km/h)`);

        // Notification for Rental Company
        const payloadRentalCompany = {
            notification: {
                title: '⚠️ Speed Limit Exceeded!',
                body: `Vehicle ${renterId} exceeded speed: ${speed}km/h (Limit: ${limit}km/h)`,
            },
            data: {
                renterId: renterId,
                speed: speed.toString(),
                limit: limit.toString(),
                latitude: latitude.toString(),
                longitude: longitude.toString()
            }
        };

        // Notification for Renter
        const payloadRenter = {
            notification: {
                title: '🚨 Speed Warning!',
                body: `You are exceeding the speed limit: ${speed}km/h (Limit: ${limit}km/h). Please slow down!`,
            },
            data: {
                renterId: renterId,
                speed: speed.toString(),
                limit: limit.toString(),
                latitude: latitude.toString(),
                longitude: longitude.toString()
            }
        };

        try {
            // 🚀 Notify Rental Company
            await admin.messaging().sendToTopic("rental-company", payloadRentalCompany);
            console.log('✅ Successfully sent notification to rental company');

            // 🚗 Notify Renter (only if userToken exists)
            if (userToken) {
                await admin.messaging().sendToDevice(userToken, payloadRenter);
                console.log(`✅ Successfully sent warning to renter: ${renterId}`);
            } else {
                console.warn(`⚠️ No FCM token found for renter ${renterId}`);
            }

        } catch (error) {
            console.error('❌ Error sending notifications:', error);
        }
    });
