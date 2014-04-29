package org.multibit.hd.ui.views.components.auto_complete;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Strings;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;

import java.util.List;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of filters for auto-complete combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AutoCompleteFilters {

  /**
   * Utilities have private constructors
   */
  private AutoCompleteFilters() {
  }

  /**
   * @param contactService    The contact service to use for queries
   * @param networkParameters The network parameters
   *
   * @return An auto-complete filter linked to the Contact API
   */
  public static AutoCompleteFilter<Recipient> newRecipientFilter(final ContactService contactService, final NetworkParameters networkParameters) {

    return new AutoCompleteFilter<Recipient>() {

      @Override
      public Recipient[] create() {

        // Only require recipients that can be paid
        List<Contact> contacts = contactService.filterContactsByContent("*", true);

        return populateRecipients(contacts);

      }

      @Override
      public Recipient[] update(String fragment) {

        if (Strings.isNullOrEmpty(fragment)) {
          return new Recipient[]{};
        }

        // Only require recipients that can be paid
        List<Contact> contacts = contactService.filterContactsByContent(fragment, true);

        return populateRecipients(contacts);
      }

      /**
       *
       * @param contacts The contacts to add to the recipients
       * @return The recipients
       */
      private Recipient[] populateRecipients(List<Contact> contacts) {

        Recipient[] recipients = new Recipient[contacts.size()];

        int i = 0;
        for (Contact contact : contacts) {
          String address = null;
          try {
            address = contact.getBitcoinAddress().orNull();
            Address bitcoinAddress = new Address(networkParameters, address);
            Recipient recipient = new Recipient(bitcoinAddress);
            recipient.setContact(contact);
            recipients[i] = recipient;
            i++;
          } catch (AddressFormatException e) {
            throw new IllegalArgumentException("Recipients must have a valid Bitcoin address ('"+address+"'). Check contact filter: "+contact, e);
          }
        }

        return recipients;
      }

    };

  }

}
