/**
 * Copyright (C) 2016 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jpomykala.javagram.requests.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Login Result
 *
 * @author Bruno Candido Volpato da Cunha
 */
@Getter
@Setter
@ToString(callSuper = true)
public class InstagramLoginResult extends StatusResult {
  private InstagramLoggedUser logged_in_user;
  private InstagramTwoFactorInfo two_factor_info;
  private InstagramChallenge challenge;

}