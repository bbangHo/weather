import {useEffect} from 'react';
import {
  requestUserPermission,
  getFcmToken,
  onMessageListener,
} from '../firebase/pushNotification';

const usePushNotification = () => {
  useEffect(() => {
    const initNotification = async () => {
      await requestUserPermission();
      await getFcmToken();
      onMessageListener();
    };

    initNotification();
  }, []);
};

export default usePushNotification;
