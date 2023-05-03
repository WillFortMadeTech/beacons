import { IEmergencyContact } from "../../entities/IEmergencyContact";
import { IOwner } from "../../entities/IOwner";
import { IUse } from "../../entities/IUse";
import { IAccountHolderResponse } from "./IAccountHolderResponse";

export interface IRegistrationResponse {
  id: string;
  hexId: string;
  status?: string;
  beaconType?: string;
  manufacturer?: string;
  createdDate?: string;
  lastModifiedDate?: string;
  model?: string;
  manufacturerSerialNumber?: string;
  chkCode?: string;
  mti?: string;
  svdr?: boolean;
  csta?: string;
  protocol?: string;
  coding?: string;
  batteryExpiryDate?: string;
  lastServicedDate?: string;
  referenceNumber: string;
  owner?: OwnerRegistrationResponse;
  accountHolder?: IAccountHolderResponse;
  uses?: UseRegistrationResponse[];
  emergencyContacts?: EmergencyContactRegistrationResponse[];
}

export type OwnerRegistrationResponse = IOwner;

export type UseRegistrationResponse = IUse;

export type EmergencyContactRegistrationResponse = IEmergencyContact;
