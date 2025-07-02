import localUnitTest from './LocalUnit.test';
import CRUDStoreTest from './CRUDStore.test';

export default function testsuite() {
  localUnitTest();
  CRUDStoreTest();
}